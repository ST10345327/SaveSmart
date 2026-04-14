/**
 * Reference:
 * - Android Developers (2024) Fragment overview. Google LLC.
 *   https://developer.android.com/guide/fragments
 * - Android Developers (2024) View Binding. Google LLC.
 *   https://developer.android.com/topic/libraries/view-binding
 */

package com.example.savesmart.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.example.savesmart.R

/**
 * Fragment for user registration (placeholder).
 *
 * GitHub commit suggestion:
 *   [auth] Create RegisterFragment placeholder
 *   - Navigation target for login screen
 *   - To be implemented with full registration flow
 *   Refs: R01, T06
 */
class RegisterFragment : Fragment() {

    companion object {
        private const val TAG = "RegisterFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView: RegisterFragment created")
        // Temporary view - will be replaced with fragment_register.xml layout
        return LinearLayout(requireContext()).apply {
            setBackgroundColor(requireContext().getColor(R.color.background))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: Initializing RegisterFragment")
    }
}

