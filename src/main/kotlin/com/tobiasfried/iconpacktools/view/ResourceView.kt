package com.tobiasfried.iconpacktools.view

import javafx.geometry.Orientation
import tornadofx.*

class ResourceView : View("Resources") {
    override val root = splitpane(Orientation.HORIZONTAL) {
        textarea {  }
        textarea {  }
        /**
         * FIELDS
         * App Name (string) = "Phosphor Carbon"
         *      GET: src/main/rest/values/strings.xml: <string name="app_name">Phosphor Carbon</string>
         *      src/main/assets/themecfg.xml: <themeName>Phosphor Carbon</themeName>
         *      src/main/assets/themeinfo.xml: <themeName>Phosphor Carbon</themeName>
         * App Version Name (string) = "1.5.6"
         *      GET: src/main/rest/values/strings.xml: <string name="version_name">1.5.6</string>
         *      src/main/assets/themecfg.xml: <version>1.5.6</version>
         *      src/main/assets/themeinfo.xml: <versionName>1.5.6</versionName>
         * App Version Code (string) = 17
         *      GET: src/main/rest/values/strings.xml: <string name="version_code">17</string>
         *      src/main/assets/app_func_theme.xml: <Theme version="@string/version_code">
         *      src/main/assets/themeinfo.xml: <versionCode>17</versionCode>
         * App Description (string) = "Phosphor Carbon is a minimal line-icon theme build on the Blueprint Dashboard."
         *      GET: src/main/rest/values/strings.xml: <string name="app_description">A minimal, friendly icon set for your home screen.
                                                        We designed Phosphor to reduce the noise and curb the reflex-driven way we use our devices.
                                                        Each icon is crafted to be clear, playful, and balanced; using minimal strokes and curves.
                                                        Scale up and down to your liking.</string>
         *      src/main/assets/themecfg.xml: <themeInfo>Phosphor Carbon is a minimal line-icon theme build on the Blueprint Dashboard.</themeInfo>
         * Package Name (string) = "com.tobiasfried.phosphor"
         *      src/main/assets/themeinfo.xml: <packageName>com.tobiasfried.phosphor</packageName>
         */

        /**
         * CREDITS
         * Credit Titles (string array)
         *      src/main/res/values/credits_configs.xml: <string-array name="credits_titles">
                                                            <item>Tobias Fried</item>
                                                            <item>Helena Zhang</item>
                                                        </string-array>
         * Credits Descriptions (string array)
         *      src/main/res/values/credits_configs.xml: <string-array name="credits_descriptions">
                                                            <item>Mobile developer, command-line noodler, and kombucha brewer in Brooklyn. </item>
                                                            <item>Designer, writer, and artist based in Brooklyn, New York. She enjoys making more with less.</item>
                                                        </string-array>
         * Credits Buttons (combobox)
         *      src/main/res/values/credits_configs.xml: <string-array name="credits_buttons">
                                                            <item>Github|Website</item>
                                                            <item>Website|Dribbble</item>
                                                        </string-array>
         * Credits Links (string array)
         *      src/main/res/values/credits_configs.xml: <string-array name="credits_links">
                                                            <item>https://github.com/rektdeckard|http://tobiasfried.com</item>
                                                            <item>http://abodywithoutorgans.com|https://dribbble.com/abodywithout</item>
                                                        </string-array>
         * Credits Photos (string array)
         *      src/main/res/values/credits_configs.xml: <string-array name="credits_photos" formatted="false" tools:ignore="TypographyDashes">
                                                            <item>https://i.imgur.com/sEmEMhx.jpg</item>
                                                            <item>https://i.imgur.com/yLiuJMJ.jpg</item>
                                                        </string-array>
         */

        /**
         * CONFIGURATIONS
         * Email (string) = "friedtm@gmail.com"
         *      src/main/res/values/blueprint_configs.xml: <string name="email">friedtm@gmail.com</string>
         * Email Subject (string) -> Mail subjects for simple e-mail
         *      src/main/res/values/blueprint_configs.xml: <string name="email_subject">@string/app_name</string>
         * Request Title (string) -> Mail subjects for requests e-mail
         *      src/main/res/values/blueprint_configs.xml: <string name="request_title">Phosphor Icon Request</string>
         * Save Location (string) -> Save location for Wallpapers and Request
         *      src/main/res/values/blueprint_configs.xml: <string name="request_save_location">%1$s/Phosphor/Requests/</string>
         * Show Info (bool) -> Show the general information in home section?
         *  TODO LATER
         */

    }
}