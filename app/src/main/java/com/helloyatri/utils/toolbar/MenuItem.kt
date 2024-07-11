package com.helloyatri.utils.toolbar

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes

data class MenuItem(var title: String? = null, @DrawableRes var icon: Int? = null,
                    val tag: String, var menuItemType: MenuItemType = MenuItemType.ICON, var showAsAction: ShowAsAction = ShowAsAction.IF_ROOM, @ColorRes var iconColor: Int? = null, @ColorRes var titleColor: Int? = null, var showBadge: Boolean = false, var badgeCount: Int = 0)

object DefaultMenuItemTag {
    const val MORE_VERT = "moreVert"
}

enum class MenuItemType {
    ICON, TITLE, BOTH
}

enum class ShowAsAction {
    NEVER, ALWAYS, IF_ROOM
}