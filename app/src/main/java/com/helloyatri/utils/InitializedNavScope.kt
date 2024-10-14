package com.helloyatri.utils
import com.google.android.libraries.navigation.Navigator
/**
 * Used with in [NavViewActivity.withNavigatorAsync] and [NavFragmentActivity.withNavigatorAsync] to
 * provide asynchronous access to the Navigator.
 */
open class InitializedNavScope(val navigatorNav: Navigator)

typealias InitializedNavRunnable = InitializedNavScope.() -> Unit
