/*
 * Copyright (C) 2018 Softbank Robotics Europe
 * See COPYING for the license
 */

package com.softbankrobotics.qisdktutorials.utils

import com.aldebaran.qi.sdk.`object`.locale.Language
import com.aldebaran.qi.sdk.`object`.locale.Locale
import com.aldebaran.qi.sdk.`object`.locale.Region

interface Constants {

    interface Intent {
        companion object {
            const val TUTORIAL_NAME_KEY = "TUTORIAL_NAME_KEY"
            const val TUTORIAL_LEVEL_KEY = "TUTORIAL_LEVEL_KEY"
        }
    }

    interface Locals {
        companion object {
            val ENGLISH_LOCALE = Locale(Language.ENGLISH, Region.UNITED_STATES)
        }
    }
}
