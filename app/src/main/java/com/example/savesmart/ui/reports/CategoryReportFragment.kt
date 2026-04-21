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
import com.example.savesmart.data.database.SaveSmartDatabase
import com.example.savesmart.data.repository.SaveSmartRepository
import com.example.savesmart.databinding.FragmentCategoryReportBinding
import com.example.savesmart.util.SessionManager
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate

/**
 * CategoryReportFragment — Visualizes spending breakdown using a Pie Chart (Requirement R17).
 *
 * GitHub commit suggestion:
 *   [reports] implement CategoryReportFragment with MPAndroidChart (R17)
 *   - Configured PieChart with percentage display
 *   - Integrated with ReportsViewModel for data observation
 *   Refs: R17, T01, T06
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
            Log.d(TAG, "onViewCreated: loading report for userId $userId")
            viewModel.loadCategoryReport(userId)
        }
    }

    private fun setupChart() {
        Log.d(TAG, "setupChart: configuring MPAndroidChart")
        binding.pieChart.apply {
            setUsePercentValues(true)
            description.isEnabled = false
            setExtraOffsets(5f, 10f, 5f, 5f)
            dragDecelerationFrictionCoef = 0.95f
            isDrawHoleEnabled = true
            setHoleColor(Color.WHITE)
            setTransparentCircleColor(Color.WHITE)
            setTransparentCircleAlpha(110)
            holeRadius = 58f
            transparentCircleRadius = 61f
            setDrawCenterText(true)
            centerText = "Spending %"
            rotationAngle = 0f
            isRotationEnabled = true
            isHighlightPerTapEnabled = true
            legend.isEnabled = true
        }
    }

    private fun observeViewModel() {
        viewModel.pieEntries.observe(viewLifecycleOwner) { entries ->
            Log.d(TAG, "observeViewModel: received ${entries.size} pie entries")
            if (entries.isNotEmpty()) {
                val dataSet = PieDataSet(entries, "Categories")
                dataSet.setColors(*ColorTemplate.MATERIAL_COLORS)
                dataSet.sliceSpace = 3f
                dataSet.selectionShift = 5f

                val data = PieData(dataSet)
                data.setValueFormatter(PercentFormatter(binding.pieChart))
                data.setValueTextSize(11f)
                data.setValueTextColor(Color.BLACK)

                binding.pieChart.data = data
                binding.pieChart.invalidate() // Refresh chart
                Log.d(TAG, "observeViewModel: chart invalidated successfully")
            } else {
                Log.w(TAG, "observeViewModel: no entries found, chart remains empty")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView: clearing binding")
        _binding = null
    }
}
