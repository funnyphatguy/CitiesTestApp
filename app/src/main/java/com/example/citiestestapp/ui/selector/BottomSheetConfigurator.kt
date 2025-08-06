package com.example.citiestestapp.ui.selector

import android.view.View
import android.view.ViewGroup
import com.google.android.material.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetConfigurator {
    fun configure(fragment: BottomSheetDialogFragment): BottomSheetBehavior<View>? {
        val dialog = fragment.dialog as? BottomSheetDialog ?: return null
        val bottomSheet = dialog.findViewById<View>(R.id.design_bottom_sheet) ?: return null

        val behavior = BottomSheetBehavior.from(bottomSheet).apply {
            isFitToContents = false
            halfExpandedRatio = 0.5f
            state = BottomSheetBehavior.STATE_HALF_EXPANDED
            isHideable = true
        }

        bottomSheet.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        bottomSheet.requestLayout()

        return behavior
    }
}