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
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
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
        viewModel = ReportsViewModel(repository)
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
            setHoleColor(Color.WHITE)
            holeRadius = 58f
            transparentCircleRadius = 61f
            setDrawCenterText(true)
            centerText = "Category %"
            legend.isEnabled = true
        }
    }

    private fun setupBarChart() {
        binding.barChart.apply {
            description.isEnabled = false
            setDrawGridBackground(false)
            setDrawBarShadow(false)
            
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)
            xAxis.granularity = 1f
            
            axisLeft.setDrawGridLines(true)
            axisRight.isEnabled = false
            
            legend.isEnabled = false
        }
    }

    private fun observeViewModel() {
        viewModel.currentMonthDisplay.observe(viewLifecycleOwner) { month ->
            binding.tvCurrentMonth.text = month
        }

        viewModel.pieEntries.observe(viewLifecycleOwner) { entries ->
            if (entries.isNotEmpty()) {
                val dataSet = PieDataSet(entries, "")
                dataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()
                dataSet.sliceSpace = 3f

                val data = PieData(dataSet)
                data.setValueFormatter(PercentFormatter(binding.pieChart))
                data.setValueTextSize(11f)
                binding.pieChart.data = data
                binding.pieChart.invalidate()
            } else {
                binding.pieChart.clear()
            }
        }

        viewModel.barEntries.observe(viewLifecycleOwner) { entries ->
            if (entries.isNotEmpty()) {
                val dataSet = BarDataSet(entries, "Daily Spending")
                
                // R18: Color logic handled by list of colors
                // We'll calculate the limit and apply colors in the next step
                dataSet.color = Color.parseColor("#1A6FE8") // Primary Blue
                
                val data = BarData(dataSet)
                data.barWidth = 0.8f
                binding.barChart.data = data
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
