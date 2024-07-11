package com.helloyatri.di.module

import android.content.Context
import com.helloyatri.di.DiConstants
import com.helloyatri.ui.base.BaseActivity
import com.helloyatri.ui.manager.FragmentHandler
import com.helloyatri.ui.manager.FragmentManager
import com.helloyatri.ui.manager.Navigator
import com.helloyatri.utils.fileselector.MediaSelectHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Named

@Module
@InstallIn(ActivityComponent::class)
object ActivityModule {

    @Provides
    @ActivityScoped
    internal fun provideBaseActivity(@ActivityContext context: Context): BaseActivity {
        return (context as BaseActivity)
    }

    @Provides
    @ActivityScoped
    internal fun provideNavigator(baseActivity: BaseActivity): Navigator {
        return baseActivity
    }

    @Provides
    @ActivityScoped
    internal fun provideFragmentHandler(fragmentManager: FragmentManager): FragmentHandler {
        return fragmentManager
    }

    @Provides
    @ActivityScoped
    @Named(DiConstants.PLACEHOLDER)
    internal fun providePlaceHolder(baseActivity: BaseActivity): Int {
        return baseActivity.findFragmentPlaceHolder()
    }

    @Provides
    @ActivityScoped
    internal fun provideImagePicker(baseActivity: BaseActivity): MediaSelectHelper {
        return MediaSelectHelper(baseActivity)
    }
}