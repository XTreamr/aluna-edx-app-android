package org.edx.mobile

import android.app.Activity
import android.app.Application
import live.govideo.app.data.dto.Environment
import live.govideo.app.data.dto.HostApp
import live.govideo.app.managers.PushManager
import live.govideo.auth.GoAuth
import live.govideo.auth.GoAuthSettings
import live.govideo.onboarding.GoOnBoarding
import live.govideo.onboarding.GoOnBoardingSettings
import live.govideo.permissions.GoPermission
import live.govideo.permissions.GoPermissionSettings
import live.govideo.tracking.GoTracking
import live.govideo.tracking.GoTrackingSettings
import live.govideo.videos.GoVideo
import live.govideo.videos.GoVideoSettings
import live.govideo.videos.VideosPushProvider

object GoLauncher {

    fun init(application: Application) {
        val environment = Environment.PRE
        val hostApp = HostApp.MOVISTAR_PLAY

        val debug = true

        //init permission
        val permissionSettings = GoPermissionSettings(
                environment = environment,
                isDebug = debug,
                theme = R.style.DefaultTheme
        )
        GoPermission.init(application, permissionSettings)

        //init auth
        val authSettings = GoAuthSettings(
                hostApp = hostApp,
                environment = environment,
                isDebug = debug,
                theme = R.style.DefaultTheme
        )
        GoAuth.init(authSettings)

        //init onboarding
        val onBoardingSettings = GoOnBoardingSettings(
                isDebug = true,
                environment = live.govideo.onboarding.Environment.DEV,
                theme = R.style.DefaultTheme,
        )

        GoOnBoarding.init(application, onBoardingSettings)

        //init tracking
        val goTrackingSettings = GoTrackingSettings(
                isDebug = true,
                environment = environment,
                hostApp = hostApp,
                theme = R.style.DefaultTheme,
                authProvider = GoAuth.getAuthProvider(application)
        )
        //add own tracking
        GoTracking.init(application, goTrackingSettings)


/*        //init quiz
        val goQuizSettings = GoQuizSettings(
                hostApp = hostApp,
                isDebug = true,
                environment = environment,
                theme = R.style.DefaultTheme,
                authProvider = GoAuth.getAuthProvider(this)
        )
        GoQuiz.init(goQuizSettings)*/

        //configure settings for feature module
        val settings = GoVideoSettings(
                hostApp = hostApp,
                environment = environment,
                isDebug = debug,
                theme = R.style.DefaultTheme,
                authProvider = GoAuth.getAuthProvider(application),
                permissionsProvider = GoPermission.permissionsProvider
        )

        //TODO el projectGo debería desaparecer, la configuración debería estar en cada módulo
        GoVideo.init(application, settings)

        PushManager.registerProvider(application, VideosPushProvider(false))
        //PushManager.registerProvider(this, QuizPushProvider(false))
    }

    fun start(activity: Activity) {
        if (!GoOnBoarding.preferenceManager.isOnboardingConsumed()) {
            GoOnBoarding.start(activity, GoVideo.getIntroStartIntent(activity))
        } else {
            GoVideo.start(activity)
        }
    }
}