/**
 * Reference:
 * - Android Developers (2024) Fragment overview. Google LLC.
 *   Available at: https://developer.android.com/guide/fragments (Accessed: 24 March 2026).
 * - Android Developers (2024) View Binding. Google LLC.
 *   Available at: https://developer.android.com/topic/libraries/view-binding (Accessed: 24 March 2026).
 */

package com.example.savesmart.ui.expense

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.savesmart.databinding.FragmentFullReceiptBinding
import java.io.File

/**
 * FullReceiptFragment — Displays a captured receipt photo in full screen (Requirement R11).
 *
 * GitHub commit suggestion:
 *   [expense] implement FullReceiptFragment for detailed receipt viewing (R11)
 *   - Added navigation from expense list to full-screen photo
 *   - Handled missing file state to prevent crashes
 *   Refs: R11, T06
 */
class FullReceiptFragment : Fragment() {

    companion object {
        private const val TAG = "FullReceiptFragment"
    }

    private var _binding: FragmentFullReceiptBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView: started")
        _binding = FragmentFullReceiptBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val photoPath = arguments?.getString("photoPath")
        Log.d(TAG, "onViewCreated: Loading photo from $photoPath")

        if (!photoPath.isNullOrEmpty()) {
            val file = File(photoPath)
            if (file.exists()) {
                binding.ivFullReceipt.setImageURI(Uri.fromFile(file))
                binding.tvEmptyState.visibility = View.GONE
            } else {
                Log.w(TAG, "onViewCreated: Photo file does not exist")
                binding.tvEmptyState.visibility = View.VISIBLE
            }
        } else {
            binding.tvEmptyState.visibility = View.VISIBLE
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView: clearing binding")
        _binding = null
    }
}
