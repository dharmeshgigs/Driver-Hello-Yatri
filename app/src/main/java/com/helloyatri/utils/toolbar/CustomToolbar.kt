package com.helloyatri.utils.toolbar

import android.content.Context
import android.graphics.Outline
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import android.widget.PopupWindow
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.helloyatri.R
import com.helloyatri.databinding.CustomToolbarBinding
import com.helloyatri.databinding.CustomToolbarPopupMenuBinding
import com.helloyatri.utils.extension.dpToPx
import com.helloyatri.utils.extension.isInVisible
import com.helloyatri.utils.extension.isVisible
import com.helloyatri.utils.hideView
import com.helloyatri.utils.showView
import com.helloyatri.utils.textdecorator.TextDecorator


class CustomToolbar(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    private val binding: CustomToolbarBinding = CustomToolbarBinding.inflate(LayoutInflater.from(context), this, true)

    // For popup menu
    private var popupWindow: PopupWindow? = null
    private val popupMenuAdapter by lazy {
        PopupMenuAdapter()
    }

    // For options menu
    private val menuItemAdapter by lazy {
        MenuItemAdapter()
    }
    private val menuItemList by lazy {
        ArrayList<MenuItem>()
    }
    private var onMenuItemClick: ((menuItem: MenuItem) -> Unit)? = null

    /* --------------- Attributes --------------- */
    private val typedArray by lazy {
        getContext().obtainStyledAttributes(attrs, R.styleable.CustomToolbar)
    }

    private var title = typedArray.getString(R.styleable.CustomToolbar_title)
    private var shortNameTitle = typedArray.getString(R.styleable.CustomToolbar_showShortTitle)

    private var showBackButton = typedArray.getBoolean(R.styleable.CustomToolbar_showBackButton, false)
    private var backButtonIcon = typedArray.getResourceId(R.styleable.CustomToolbar_backButtonIcon, DEFAULT_BACK_BUTTON_ICON)

    private var showHamburgerIcon = typedArray.getBoolean(R.styleable.CustomToolbar_showHamburgerIcon, false)
    private var hamburgerIcon = typedArray.getResourceId(R.styleable.CustomToolbar_hamburgerIcon, DEFAULT_HAMBURGER_ICON)

    private var showAppName = typedArray.getBoolean(R.styleable.CustomToolbar_showAppName, false)

    private var toolbarColor = typedArray.getResourceId(R.styleable.CustomToolbar_toolbarColor, DEFAULT_TOOLBAR_COLOR)

    private var toolbarTitleColor = typedArray.getResourceId(R.styleable.CustomToolbar_toolbarTitleColor, DEFAULT_TOOLBAR_TITLE_COLOR)

    private var moreOptionIcon = typedArray.getResourceId(R.styleable.CustomToolbar_moreOptionIcon, DEFAULT_MORE_OPTION_ICON)
    private var maxItemAllowed = typedArray.getInteger(R.styleable.CustomToolbar_maxItemAllowed, DEFAULT_MAX_ITEM_ALLOWED)

    private var popupMenuElevation = typedArray.getDimension(R.styleable.CustomToolbar_popupMenuElevation, resources.getDimension(DEFAULT_POPUP_MENU_ELEVATION))
    private var popupMenuCornerRadius = typedArray.getDimension(R.styleable.CustomToolbar_popupMenuCornerRadius, resources.getDimension(DEFAULT_POPUP_MENU_CORNER_RADIUS))
    private var popupMenuBackgroundColor = typedArray.getResourceId(R.styleable.CustomToolbar_popupMenuBackgroundColor, DEFAULT_POPUP_MENU_BACKGROUND_COLOR)

    private var textDecorator: TextDecorator? = null

    init {
        typedArray.recycle()
    }

    // Handling of back button
    private fun showBackButton() = with(binding) {
        imageViewBackButton.setImageResource(backButtonIcon)
        imageViewBackButton.isInVisible(!showBackButton || showHamburgerIcon)
    }

    fun showBackButton(showBackButton: Boolean): CustomToolbar {
        this.showBackButton = showBackButton

        return this
    }

    fun setBackButtonIcon(@DrawableRes backButtonIcon: Int): CustomToolbar {
        this.backButtonIcon = backButtonIcon

        return this
    }

    fun setOnBackButtonClickListener(onBackButtonClick: () -> Unit) = with(binding) {
        if (showBackButton && !showHamburgerIcon) {
            imageViewBackButton.setOnClickListener {
                onBackButtonClick()
            }
        }
    }

    // Handling of Menu Icon
    private fun showHamburgerIcon() = with(binding) {
        imageViewMenuIcon.setImageResource(hamburgerIcon)
        imageViewMenuIcon.isVisible(showHamburgerIcon)
    }

    fun showHamburgerIcon(showMenuIcon: Boolean): CustomToolbar {
        this.showHamburgerIcon = showMenuIcon

        return this
    }

    fun setOnHamburgerIconClickListener(onHamburgerIconClick: () -> Unit) = with(binding) {
        imageViewMenuIcon.setOnClickListener {
            onHamburgerIconClick()
        }
    }

    fun setOnShortNameClickListener(onShortNameClick: () -> Unit) = with(binding) {
        textViewNameShort.setOnClickListener {
            onShortNameClick()
        }
    }

    fun showAppNameWithIcon() = with(binding) {
        textViewHomeAppName.isVisible(showAppName)
        textViewToolbarTitle.isVisible(!showAppName)
    }

    fun setHamburgerIcon(@DrawableRes hamburgerIcon: Int): CustomToolbar {
        this.hamburgerIcon = hamburgerIcon

        return this
    }

    // Handling of Toolbar title
    private fun handleToolbarTitle() = with(binding) {
        title?.let {
            showView(textViewToolbarTitle)
            textViewToolbarTitle.text = title
        } ?: run {
            hideView(textViewToolbarTitle)
        }
    }

    private fun handleShortName() = with(binding) {
        shortNameTitle?.let {
            showView(textViewNameShort)
            textViewNameShort.text = shortNameTitle
        } ?: run {
            hideView(textViewNameShort)
        }
    }

    /**
     * Use to set the toolbar title
     * @param title Pass the title you want to display.
     *
     * */
    fun setToolbarTitle(title: String): CustomToolbar {
        this.title = title

        return this
    }

    /**
     * Use to set the toolbar title
     * @param title Pass the StringRes which you want to display as title.
     * */
    fun setToolbarTitle(@StringRes title: Int): CustomToolbar {
        this.title = context.getString(title)

        return this
    }

    fun setShortNameTitle(title: String?): CustomToolbar {
        this.shortNameTitle = title

        return this
    }


    // Toolbar color
    private fun handleToolbarColor() = with(binding) {
        toolbar.setBackgroundColor(resources.getColor(toolbarColor, null))
    }

    fun setToolbarColor(@ColorRes toolbarColor: Int): CustomToolbar {
        this.toolbarColor = toolbarColor

        return this
    }

    // Toolbar title color
    private fun handleToolbarTitleColor() = with(binding) {
        textViewToolbarTitle.setTextColor(ResourcesCompat.getColor(resources, toolbarTitleColor, null))
    }

    fun setToolbarTitleColor(@ColorRes toolbarTitleColor: Int): CustomToolbar {
        this.toolbarTitleColor = toolbarTitleColor

        return this
    }

    /**
     * This method is use to set title on the toolbar and then it will decorate the title with the help of [TextDecorator]
     *
     * Note: It has higher priority than [setToolbarTitle].
     *
     * @param title Title which you want to set on toolbar
     * @param decorateToolbarTitle It will give you [TextDecorator] instance. Call the appropriate method on it.
     * Do not call build method on it, it will be called automatically.
     *
     * @see TextDecorator
     * */
    fun setAndDecorateToolbarTitle(title: String, decorateToolbarTitle: (textDecorator: TextDecorator) -> TextDecorator): CustomToolbar {
        this.textDecorator = decorateToolbarTitle(TextDecorator.decorate(binding.textViewToolbarTitle, title))

        return this
    }

    fun setAndDecorateAppTitle(title: String, decorateToolbarTitle: (textDecorator: TextDecorator) -> TextDecorator): CustomToolbar {
        this.textDecorator = decorateToolbarTitle(TextDecorator.decorate(binding.textViewHomeAppName, title))

        return this
    }

    /* --------------- Options Menu --------------- */
    private fun setUpOptionsMenu() {
        setUpMenuItemRecyclerView()
        setUpPopupMenu()
    }

    fun setMoreOptionIcon(@DrawableRes moreOptionIcon: Int): CustomToolbar {
        this.moreOptionIcon = moreOptionIcon

        return this
    }

    private fun setUpMenuItemRecyclerView() = with(binding) {
        recyclerViewOptionsMenu.apply {
            adapter = menuItemAdapter
            layoutManager = LinearLayoutManager(this.context, RecyclerView.HORIZONTAL, false)
            menuItemAdapter.errorMessage = ""
        }

        setUpMenuItemClickListener()
    }

    fun addMenuItems(vararg menuItemList: MenuItem): CustomToolbar {
        this.menuItemList.addAll(menuItemList)

        return this
    }

    fun updateMenuItem(menuItem: MenuItem, predicate: (MenuItem) -> Boolean): CustomToolbar {
        menuItemAdapter.updateItem(menuItem, predicate = predicate)
        popupMenuAdapter.updateItem(menuItem, predicate = predicate)

        return this
    }

    fun updateMenuItem(predicate: (MenuItem) -> Boolean, menuItemToUpdate: (MenuItem) -> Unit): CustomToolbar {
        menuItemAdapter.updateItem(predicate = predicate, itemToUpdate = menuItemToUpdate)
        popupMenuAdapter.updateItem(predicate = predicate, itemToUpdate = menuItemToUpdate)

        return this
    }

    private fun setUpMenuItemClickListener() {
        menuItemAdapter.setOnItemClickListener {
            when (it.tag) {
                DefaultMenuItemTag.MORE_VERT -> {
                    showPopupMenu()
                }

                else -> onMenuItemClick?.invoke(it)
            }
        }
    }

    fun setOnMenuItemClickListener(onMenuItemClick: (menuItem: MenuItem) -> Unit) {
        this.onMenuItemClick = onMenuItemClick
    }

    private fun setUpPopupMenu() {
        val popupMenuBinding = CustomToolbarPopupMenuBinding.inflate(LayoutInflater.from(context))
        popupWindow = PopupWindow(popupMenuBinding.root, ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT, true)

        popupWindow?.elevation = popupMenuElevation

        with(popupMenuBinding) {
            recyclerViewPopupMenu.apply {
                adapter = popupMenuAdapter
                layoutManager = LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)
                popupMenuAdapter.errorMessage = ""
            }

            cardView.radius = popupMenuCornerRadius
            cardView.setCardBackgroundColor(resources.getColor(popupMenuBackgroundColor, null))
        }

        popupMenuAdapter.setOnItemClickListener {
            onMenuItemClick?.invoke(it)
            popupWindow?.dismiss()
        }
    }

    private fun showPopupMenu() {
        // Placing popup menu below MenuIconMore
        popupWindow?.showAsDropDown(binding.recyclerViewOptionsMenu, -(resources.getDimensionPixelOffset(R.dimen._180sdp)), resources.getDimensionPixelSize(R.dimen._1sdp), Gravity.END)
    }

    /**
     * @param maxItemAllowed Number of icons you want to display at max on toolbar.
     * */
    fun setMaxItemAllowed(maxItemAllowed: Int): CustomToolbar {
        this.maxItemAllowed = maxItemAllowed

        return this
    }

    private fun setMenuItems() {
        val menuItemsToShowOnPopupMenu = menuItemList.filter { it.showAsAction == ShowAsAction.NEVER }.toMutableList() as ArrayList<MenuItem>
        val menuItemsToShowOnToolbar = menuItemList.filter { it.showAsAction == ShowAsAction.ALWAYS }.toMutableList() as ArrayList<MenuItem>
        val menuItemsToShowOnToolbarIfRoom = menuItemList.filter { it.showAsAction == ShowAsAction.IF_ROOM }.toMutableList() as ArrayList<MenuItem>

        /** Checks if items which can be shown on toolbar are more than [maxItemAllowed] */
        if (menuItemList.size - menuItemsToShowOnPopupMenu.size > maxItemAllowed) {
            // Finds the number of item to show on popup menu.

            // Example: If there are total 10 menu items, among which there are:
            // 4 - Never, 5 - IfRoom and 1 - Always
            // And maxItemAllowed value is 3
            // Then this variable will evaluates to: 10 - 4 - 3 + 1 = 4
            val itemsToFind = menuItemList.size - menuItemsToShowOnPopupMenu.size - maxItemAllowed + 1

            // So it will add last 4 items(if any) from the list in which the showAsAction value is IF_ROOM.
            menuItemsToShowOnPopupMenu.addAll(menuItemsToShowOnToolbarIfRoom.takeLast(itemsToFind))

            // This condition will evaluates to: (5 - 4 = 1), so it will enter in this if condition.
            if (menuItemsToShowOnToolbarIfRoom.size - itemsToFind > 0) {
                // Now it will take a sublist starting from 0th position to (5 - 4 = 1) position
                // (i.e. 0th to 1st position)
                // And then add it to the menuItemsToShowOnToolbar list.
                menuItemsToShowOnToolbar.addAll(menuItemsToShowOnToolbarIfRoom.subList(0, menuItemsToShowOnToolbarIfRoom.size - itemsToFind))
            }
        } else {
            // if items which can be shown on toolbar are less than maxItemAllowed
            // Then it will add all the items which can be shown on toolbar ifRoom
            // to the menuItemsToShowOnToolbar list
            menuItemsToShowOnToolbar.addAll(menuItemsToShowOnToolbarIfRoom)
        }

        // So now we have the list of items which can be shown on toolbar
        // So lets add it to the menuItemAdapter,
        // So that it can display on toolbar.
        menuItemAdapter.setItems(menuItemsToShowOnToolbar, 1)

        // Again 1 condition to check if there are any items which are supposed to show
        // on popup menu.
        if (menuItemsToShowOnPopupMenu.isNotEmpty()) {
            // If there are any then add it to the popupMenuAdapter list.
            popupMenuAdapter.setItems(menuItemsToShowOnPopupMenu, 1)

            // And add more option icon to menuItemAdapter
            menuItemAdapter.addItem(MenuItem(title = context.getString(R.string.title_more), icon = moreOptionIcon, tag = DefaultMenuItemTag.MORE_VERT))
        }
    }

    fun setPopupMenuElevation(@DimenRes popupMenuElevation: Int): CustomToolbar {
        this.popupMenuElevation = resources.getDimension(popupMenuElevation)

        return this
    }

    fun setPopupMenuCornerRadius(@DimenRes popupMenuCornerRadius: Int): CustomToolbar {
        this.popupMenuCornerRadius = resources.getDimension(popupMenuCornerRadius)

        return this
    }

    fun setPopupMenuBackgroundColor(@ColorRes popupMenuBackgroundColor: Int): CustomToolbar {
        this.popupMenuBackgroundColor = popupMenuBackgroundColor

        return this
    }

    fun setToolbarElevation(elevation: Int): CustomToolbar {
        this.elevation = resources.dpToPx(elevation).toFloat()

        return this
    }

    // It set the padding horizontal on the toolbar title based on available space.
    private fun setPaddingOnToolbarTitle() = with(binding) {
        post {
            val padding = recyclerViewOptionsMenu.measuredWidth + resources.dpToPx(R.dimen._10sdp)
            textViewToolbarTitle.updatePadding(left = padding, right = padding)
        }
    }

    fun build() {
        menuItemAdapter.clearAllItem()
        popupMenuAdapter.clearAllItem()

        showBackButton()
        handleShortName()
        handleToolbarTitle()
        handleToolbarColor()
        handleToolbarTitleColor()
        showHamburgerIcon()
        setUpOptionsMenu()
        showAppNameWithIcon()
        setMenuItems()
        setPaddingOnToolbarTitle()
        textDecorator?.build()
    }

    fun reset() {
        setToolbarTitle("")
        setShortNameTitle(null)
        handleShortName()
        setToolbarColor(DEFAULT_TOOLBAR_COLOR)
        setToolbarTitleColor(DEFAULT_TOOLBAR_TITLE_COLOR)
        showBackButton(false)
        showBackButton()
        showHamburgerIcon(false)
        showHamburgerIcon()
        setMaxItemAllowed(DEFAULT_MAX_ITEM_ALLOWED)
        textDecorator = null
        elevation = 0f
        menuItemList.clear()
        popupMenuAdapter.clearAllItem()
        menuItemAdapter.clearAllItem()
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        outlineProvider = CustomOutline(width, height)
    }

    private class CustomOutline(var width: Int, var height: Int) : ViewOutlineProvider() {

        override fun getOutline(view: View?, outline: Outline) {
            outline.setRect(0, 0, width, height)
        }
    }

    companion object {
        const val DEFAULT_MAX_ITEM_ALLOWED = 3
        const val DEFAULT_POPUP_MENU_ELEVATION = R.dimen._6sdp
        const val DEFAULT_POPUP_MENU_CORNER_RADIUS = R.dimen._6sdp
        const val DEFAULT_POPUP_MENU_BACKGROUND_COLOR = R.color.white
        const val DEFAULT_TOOLBAR_COLOR = R.color.white
        const val DEFAULT_TOOLBAR_TITLE_COLOR = R.color.black
        const val DEFAULT_BACK_BUTTON_ICON = R.drawable.ic_back
        const val DEFAULT_HAMBURGER_ICON = R.drawable.ic_side_menu
        const val DEFAULT_MORE_OPTION_ICON = R.drawable.ic_more_vert
    }
}
