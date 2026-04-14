/**
 * Reference:
 * - Android Developers (2024) Fragment overview. Google LLC.
 *   https://developer.android.com/guide/fragments
 * - Android Developers (2024) View Binding. Google LLC.
 *   https://developer.android.com/topic/libraries/view-binding
 */

package com.example.savesmart.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.example.savesmart.R

/**
 * Fragment for displaying user dashboard (placeholder).
 *
 * GitHub commit suggestion:
 *   [dashboard] Create DashboardFragment placeholder
 *   - Navigation target for successful login
 *   - To be implemented with dashboard widgets and charts
 *   Refs: R15, T06
 */
class DashboardFragment : Fragment() {

    companion object {
        private const val TAG = "DashboardFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView: DashboardFragment created")
        // Temporary view - will be replaced with fragment_dashboard.xml layout
        return LinearLayout(requireContext()).apply {
            setBackgroundColor(requireContext().getColor(R.color.background))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: Initializing DashboardFragment")
    }
}

