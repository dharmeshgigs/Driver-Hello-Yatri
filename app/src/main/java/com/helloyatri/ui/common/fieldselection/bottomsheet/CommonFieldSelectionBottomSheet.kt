package com.helloyatri.ui.common.fieldselection.bottomsheet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.helloyatri.R
import com.helloyatri.databinding.CommonFieldSelectionBottomsheetBinding
import com.helloyatri.ui.base.BaseBottomSheetDialogFragment
import com.helloyatri.ui.common.fieldselection.adapter.CommonFieldSelectionBottomSheetAdapter
import com.helloyatri.ui.common.fieldselection.data.CommonFieldSelection
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CommonFieldSelectionBottomSheet :
        BaseBottomSheetDialogFragment<CommonFieldSelectionBottomsheetBinding>() {

    private var onOkButtonClick: ((selectedOption: CommonFieldSelection) -> Unit)? = null

    private var selectedOption: String? = null

    private var setTitle: String? = null

    private val commonFieldSelectionBottomSheetAdapter by lazy {
        CommonFieldSelectionBottomSheetAdapter()
    }

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?,
                                   attachToRoot: Boolean): CommonFieldSelectionBottomsheetBinding {
        return CommonFieldSelectionBottomsheetBinding.inflate(layoutInflater)
    }

    override fun bindData() {
        setBottomSheetTitle()
        setUpRecyclerView()
        setUpClickListener()
    }

    private fun setUpClickListener() = with(binding) {
        buttonOkay.setOnClickListener {
            if (!commonFieldSelectionBottomSheetAdapter.items!!.any { it.isOptionSelected }) {
                showMessage(getString(R.string.validation_please_select_at_least_one_option))
            } else {
                commonFieldSelectionBottomSheetAdapter.items!!.find { it.isOptionSelected }?.let {
                    onOkButtonClick?.invoke(it)
                    dismiss()
                }
            }
        }
    }

    private fun setBottomSheetTitle() = with(binding) {
        textViewTitle.text = setTitle
    }

    private fun setUpRecyclerView() = with(binding) {
        recyclerViewSelectOptions.apply {
            adapter = commonFieldSelectionBottomSheetAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        }

        commonFieldSelectionBottomSheetAdapter.updateItem(
                predicate = { it.options == selectedOption }) {
            it.isOptionSelected = true
        }
    }

    fun setOnOkButtonClickListener(
            onOkButtonClick: (selectedOption: CommonFieldSelection) -> Unit) {
        this.onOkButtonClick = onOkButtonClick
    }

    fun setOptionsList(
            optionList: ArrayList<CommonFieldSelection>): CommonFieldSelectionBottomSheet {
        commonFieldSelectionBottomSheetAdapter.setItems(optionList, 1)
        return this
    }

    fun setSelectedOption(selectedOption: String?): CommonFieldSelectionBottomSheet {
        this.selectedOption = selectedOption
        return this
    }

    fun setTitle(setTitle: String?): CommonFieldSelectionBottomSheet {
        this.setTitle = setTitle
        return this
    }
}