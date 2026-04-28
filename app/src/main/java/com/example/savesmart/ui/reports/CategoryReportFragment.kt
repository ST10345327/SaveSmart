/**
 * References:
 * - Jahoda, P. (2024) MPAndroidChart. GitHub.
 *   Available at: https://github.com/PhilJay/MPAndroidChart (Accessed: 24 March 2026).
 * - Android Developers (2024) View Binding. Google LLC.
 *   Available at: https://developer.android.com/topic/libraries/view-binding (Accessed: 24 March 2026).
 */

package com.example.savesmart.ui.reports

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.savesmart.R
import com.example.savesmart.data.database.SaveSmartDatabase
import com.example.savesmart.data.repository.SaveSmartRepository
import com.example.savesmart.databinding.FragmentCategoryReportBinding
import com.example.savesmart.util.SessionManager
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate

/**
 * CategoryReportFragment — Visualizes spending breakdown using Pie and Bar Charts (R17, R18).
 */
class CategoryReportFragment : Fragment() {

    private var _binding: FragmentCategoryReportBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ReportsViewModel
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = SaveSmartDatabase.getInstance(requireContext())
        val repository = SaveSmartRepository(db)
        val factory = com.example.savesmart.ui.ViewModelFactory(repository)
        viewModel = androidx.lifecycle.ViewModelProvider(this, factory)[ReportsViewModel::class.java]
        sessionManager = SessionManager(requireContext())

        setupPieChart()
        setupBarChart()
        
        binding.btnPrevMonth.setOnClickListener { viewModel.prevMonth() }
        binding.btnNextMonth.setOnClickListener { viewModel.nextMonth() }
        
        observeViewModel()

        val userId = sessionManager.getUserId()
        if (userId != -1) {
            viewModel.loadCategoryReport(userId)
        }
    }

    private fun setupPieChart() {
        binding.pieChart.apply {
            setUsePercentValues(true)
            description.isEnabled = false
            setExtraOffsets(5f, 10f, 5f, 5f)
            
            isDrawHoleEnabled = true
            setHoleColor(Color.TRANSPARENT)
            holeRadius = 58f
            transparentCircleRadius = 61f
            
            setDrawCenterText(true)
            centerText = "Category Breakdown"
            setCenterTextColor(Color.GRAY)
            setCenterTextSize(16f)

            // Animation for better UX
            animateY(1400, com.github.mikephil.charting.animation.Easing.EaseInOutQuad)

            legend.apply {
                isEnabled = true
                verticalAlignment = com.github.mikephil.charting.components.Legend.LegendVerticalAlignment.BOTTOM
                horizontalAlignment = com.github.mikephil.charting.components.Legend.LegendHorizontalAlignment.CENTER
                orientation = com.github.mikephil.charting.components.Legend.LegendOrientation.HORIZONTAL
                setDrawInside(false)
                xEntrySpace = 7f
                yEntrySpace = 0f
                yOffset = 0f
                isWordWrapEnabled = true
            }

            // Enable touch and tooltips
            setTouchEnabled(true)
            setOnChartValueSelectedListener(object : com.github.mikephil.charting.listener.OnChartValueSelectedListener {
                override fun onValueSelected(e: com.github.mikephil.charting.data.Entry?, h: com.github.mikephil.charting.highlight.Highlight?) {
                    if (e == null) return
                    val pieEntry = e as PieEntry
                    centerText = "${pieEntry.label}\n${String.format(java.util.Locale.getDefault(), "%.1f", e.value)}%"
                }
                override fun onNothingSelected() {
                    centerText = "Category Breakdown"
                }
            })
        }
    }

    private fun setupBarChart() {
        binding.barChart.apply {
            description.isEnabled = false
            setDrawGridBackground(false)
            setDrawBarShadow(false)
            
            // Interaction
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(true)

            // Animation
            animateY(1000)
            
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
                textColor = Color.GRAY
            }
            
            axisLeft.apply {
                setDrawGridLines(true)
                textColor = Color.GRAY
                axisMinimum = 0f
            }
            
            axisRight.isEnabled = false
            legend.isEnabled = false
        }
    }

    private fun updatePieChart(entries: List<PieEntry>, colors: List<Int>) {
        if (entries.isNotEmpty() && colors.isNotEmpty() && entries.size == colors.size) {
            val dataSet = PieDataSet(entries, "")
            dataSet.colors = colors
            dataSet.sliceSpace = 3f
            dataSet.valueTextColor = Color.WHITE
            dataSet.valueTextSize = 12f

            val data = PieData(dataSet)
            data.setValueFormatter(PercentFormatter(binding.pieChart))
            binding.pieChart.data = data
            binding.pieChart.invalidate()
        } else {
            binding.pieChart.clear()
        }
    }

    private fun observeViewModel() {
        viewModel.currentMonthDisplay.observe(viewLifecycleOwner) { month ->
            binding.tvCurrentMonth.text = month
        }

        viewModel.pieEntries.observe(viewLifecycleOwner) { entries ->
            updatePieChart(entries, viewModel.pieColors.value ?: emptyList())
        }

        viewModel.pieColors.observe(viewLifecycleOwner) { colors ->
            updatePieChart(viewModel.pieEntries.value ?: emptyList(), colors)
        }

        viewModel.barEntries.observe(viewLifecycleOwner) { entries ->
            if (entries.isNotEmpty()) {
                val dataSet = BarDataSet(entries, "Daily Spending")
                
                // R18: Apply dynamic colors based on threshold
                val limit = viewModel.dailyLimit.value ?: 0f
                val colors = entries.map { entry ->
                    if (entry.y > limit) Color.parseColor("#B91C1C") // Over Red
                    else Color.parseColor("#1A6FE8") // Primary Blue
                }
                dataSet.colors = colors
                dataSet.setDrawValues(false) // Keep it clean
                
                val data = BarData(dataSet)
                data.barWidth = 0.8f
                binding.barChart.data = data
                binding.barChart.highlightValue(null) // Clear selection
                binding.barChart.invalidate()
            } else {
                binding.barChart.clear()
            }
        }

        viewModel.dailyLimit.observe(viewLifecycleOwner) { limit ->
            // R18: Add dashed horizontal threshold line
            val axis = binding.barChart.axisLeft
            axis.removeAllLimitLines()
            
            val limitLine = LimitLine(limit, "Daily Budget")
            limitLine.lineColor = Color.RED
            limitLine.lineWidth = 2f
            limitLine.enableDashedLine(10f, 10f, 0f)
            limitLine.labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP
            limitLine.textSize = 10f
            
            axis.addLimitLine(limitLine)
            binding.barChart.invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
