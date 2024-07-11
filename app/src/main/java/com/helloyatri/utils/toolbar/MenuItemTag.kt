package com.helloyatri.utils.toolbar

sealed class MenuItemTag : Tag {
    object Search : MenuItemTag()
    object Notification : MenuItemTag()
    object Filter : MenuItemTag()
}
