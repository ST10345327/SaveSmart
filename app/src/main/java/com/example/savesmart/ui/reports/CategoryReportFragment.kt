/**
 * References:
 * - Jahoda, P. (2024) MPAndroidChart. GitHub.
 *   Available at: https://github.com/PhilJay/MPAndroidChart (Accessed: 24 March 2026).
 * - Android Developers (2024) View Binding. Google LLC.
 *   Available at: https://developer.android.com/topic/libraries/view-binding (Accessed: 24 March 2026).
 * - Material Design (2024) Material Design 3. Google LLC.
 *   Available at: https://m3.material.io (Accessed: 24 March 2026).
 */

package com.example.savesmart.ui.reports

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.savesmart.data.database.SaveSmartDatabase
import com.example.savesmart.data.repository.SaveSmartRepository
import com.example.savesmart.databinding.FragmentCategoryReportBinding
import com.example.savesmart.util.SessionManager
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate

/**
 * CategoryReportFragment — Displays spending breakdown using a PieChart (Requirement R17).
 *
 * GitHub commit suggestion:
 *   [reports] implement CategoryReportFragment with MPAndroidChart integration
 *   - Configured PieChart with Material Design colors
 *   - Observed aggregated spending data from ReportsViewModel
 *   Refs: R17, T01, T06, T09
 */
class CategoryReportFragment : Fragment() {

    companion object {
        private const val TAG = "CategoryReportFragment"
    }

    private var _binding: FragmentCategoryReportBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ReportsViewModel
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView: started")
        _binding = FragmentCategoryReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: started")

        val db = SaveSmartDatabase.getInstance(requireContext())
        val repository = SaveSmartRepository(db)
        viewModel = ReportsViewModel(repository)
        sessionManager = SessionManager(requireContext())

        setupChart()
        observeViewModel()

        val userId = sessionManager.getUserId()
        if (userId != -1) {
            viewModel.loadCategoryReport(userId)
        }
    }

    private fun setupChart() {
        Log.d(TAG, "setupChart: configuring pie chart")
        binding.pieChart.apply {
            setUsePercentValues(true)
            description.isEnabled = false
            setExtraOffsets(5f, 10f, 5f, 5f)
            dragDecelerationFrictionCoef = 0.95f
            
            isDrawHoleEnabled = true
            setHoleColor(Color.TRANSPARENT)
            setTransparentCircleColor(Color.WHITE)
            setTransparentCircleAlpha(110)
            
            holeRadius = 58f
            transparentCircleRadius = 61f
            
            setDrawCenterText(true)
            rotationAngle = 0f
            isRotationEnabled = true
            isHighlightPerTapEnabled = true
            
            animateY(1400, Easing.EaseInOutQuad)
            
            legend.isEnabled = true
            setEntryLabelColor(Color.WHITE)
            setEntryLabelTextSize(12f)
        }
    }

    private fun observeViewModel() {
        viewModel.pieEntries.observe(viewLifecycleOwner) { entries ->
            Log.d(TAG, "observeViewModel: received ${entries.size} pie entries")
            
            if (entries.isEmpty()) {
                Log.w(TAG, "observeViewModel: no data to display")
                // Handle empty state UI if needed
                return@observe
            }

            val dataSet = PieDataSet(entries, "Categories")
            dataSet.sliceSpace = 3f
            dataSet.selectionShift = 5f
            
            // Custom colors matching the design system
            val colors = mutableListOf<Int>()
            for (c in ColorTemplate.MATERIAL_COLORS) colors.add(c)
            for (c in ColorTemplate.VORDIPLOM_COLORS) colors.add(c)
            dataSet.colors = colors

            val data = PieData(dataSet)
            data.setValueFormatter(PercentFormatter(binding.pieChart))
            data.setValueTextSize(11f)
            data.setValueTextColor(Color.WHITE)
            
            binding.pieChart.data = data
            binding.pieChart.invalidate() // Refresh the chart
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView: clearing binding")
        _binding = null // T06: Prevent memory leaks
    }
}
