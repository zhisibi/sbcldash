package com.example.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import com.example.R

enum class ThemePresetId(
    val id: String,
    val nameZh: String,
    val nameEn: String,
    val primaryColor: Color,
    val surfaceColor: Color,
    val lightColorScheme: ColorScheme,
    val darkColorScheme: ColorScheme
) {
    DEFAULT_VIOLET(
        id = "DEFAULT_VIOLET",
        nameZh = "紫罗兰 (默认)",
        nameEn = "Classic Violet",
        primaryColor = Color(0xFF6750A4),
        surfaceColor = Color(0xFFF3EDF7),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFF6750A4),
            onPrimary = Color(0xFFFFFFFF),
            primaryContainer = Color(0xFFEADDFF),
            onPrimaryContainer = Color(0xFF21005D),
            secondary = Color(0xFF625B71),
            secondaryContainer = Color(0xFFE8DEF8),
            onSecondaryContainer = Color(0xFF1D192B),
            tertiary = Color(0xFF7D5260),
            tertiaryContainer = Color(0xFFFFD8E4),
            onTertiaryContainer = Color(0xFF31111D),
            background = Color(0xFFFDFBFF),
            surface = Color(0xFFFFFFFF),
            surfaceVariant = Color(0xFFF3EDF7),
            onBackground = Color(0xFF1C1B1F),
            onSurface = Color(0xFF1D1B20)
        ),
        darkColorScheme = darkColorScheme(
            primary = Color(0xFFD0BCFF),
            onPrimary = Color(0xFF381E72),
            primaryContainer = Color(0xFF4F378B),
            onPrimaryContainer = Color(0xFFEADDFF),
            secondary = Color(0xFFCCC2DC),
            secondaryContainer = Color(0xFF4A4458),
            onSecondaryContainer = Color(0xFFE8DEF8),
            tertiary = Color(0xFFEFB8C8),
            tertiaryContainer = Color(0xFF633B48),
            onTertiaryContainer = Color(0xFFFFD8E4),
            background = Color(0xFF141218),
            surface = Color(0xFF211F26),
            surfaceVariant = Color(0xFF2B2930),
            onBackground = Color(0xFFE6E0E9),
            onSurface = Color(0xFFE6E0E9)
        )
    ),

    CYBER_NEON(
        id = "CYBER_NEON",
        nameZh = "赛博霓虹",
        nameEn = "Cyber Neon",
        primaryColor = Color(0xFF00E5FF),
        surfaceColor = Color(0xFF101B2B),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFF00838F),
            onPrimary = Color(0xFFFFFFFF),
            primaryContainer = Color(0xFFB2EBF2),
            onPrimaryContainer = Color(0xFF002025),
            secondary = Color(0xFFE91E63),
            secondaryContainer = Color(0xFFF8BBD0),
            onSecondaryContainer = Color(0xFF310012),
            tertiary = Color(0xFF7C4DFF),
            tertiaryContainer = Color(0xFFEDE7F6),
            onTertiaryContainer = Color(0xFF1A005B),
            background = Color(0xFFF0FDFD),
            surface = Color(0xFFFFFFFF),
            surfaceVariant = Color(0xFFE0F7FA),
            onBackground = Color(0xFF001F24),
            onSurface = Color(0xFF001F24)
        ),
        darkColorScheme = darkColorScheme(
            primary = Color(0xFF00E5FF),
            onPrimary = Color(0xFF00363A),
            primaryContainer = Color(0xFF004F53),
            onPrimaryContainer = Color(0xFFB2EBF2),
            secondary = Color(0xFFFF4081),
            secondaryContainer = Color(0xFF880E4F),
            onSecondaryContainer = Color(0xFFFFD1E8),
            tertiary = Color(0xFFB388FF),
            tertiaryContainer = Color(0xFF4527A0),
            onTertiaryContainer = Color(0xFFEDE7F6),
            background = Color(0xFF0A0E17),
            surface = Color(0xFF101726),
            surfaceVariant = Color(0xFF182338),
            onBackground = Color(0xFFE0F7FA),
            onSurface = Color(0xFFE0F7FA)
        )
    ),

    OCEAN_DEEP(
        id = "OCEAN_DEEP",
        nameZh = "深海沉静",
        nameEn = "Ocean Deep",
        primaryColor = Color(0xFF0288D1),
        surfaceColor = Color(0xFF0F1E2E),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFF0277BD),
            onPrimary = Color(0xFFFFFFFF),
            primaryContainer = Color(0xFFE1F5FE),
            onPrimaryContainer = Color(0xFF001F33),
            secondary = Color(0xFF0097A7),
            secondaryContainer = Color(0xFFE0F7FA),
            onSecondaryContainer = Color(0xFF002025),
            tertiary = Color(0xFF26A69A),
            tertiaryContainer = Color(0xFFE0F2F1),
            onTertiaryContainer = Color(0xFF00201A),
            background = Color(0xFFF4F9FC),
            surface = Color(0xFFFFFFFF),
            surfaceVariant = Color(0xFFE1F5FE),
            onBackground = Color(0xFF001F33),
            onSurface = Color(0xFF001F33)
        ),
        darkColorScheme = darkColorScheme(
            primary = Color(0xFF4FC3F7),
            onPrimary = Color(0xFF00344A),
            primaryContainer = Color(0xFF004C6D),
            onPrimaryContainer = Color(0xFFE1F5FE),
            secondary = Color(0xFF80DEEA),
            secondaryContainer = Color(0xFF004D40),
            onSecondaryContainer = Color(0xFFE0F7FA),
            tertiary = Color(0xFF80CBC4),
            tertiaryContainer = Color(0xFF004D40),
            onTertiaryContainer = Color(0xFFE0F2F1),
            background = Color(0xFF0A131D),
            surface = Color(0xFF101E2B),
            surfaceVariant = Color(0xFF182A3C),
            onBackground = Color(0xFFE1F5FE),
            onSurface = Color(0xFFE1F5FE)
        )
    ),

    FOREST_JADE(
        id = "FOREST_JADE",
        nameZh = "翡翠森林",
        nameEn = "Forest Jade",
        primaryColor = Color(0xFF2E7D32),
        surfaceColor = Color(0xFF112213),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFF2E7D32),
            onPrimary = Color(0xFFFFFFFF),
            primaryContainer = Color(0xFFE8F5E9),
            onPrimaryContainer = Color(0xFF002204),
            secondary = Color(0xFF558B2F),
            secondaryContainer = Color(0xFFF1F8E9),
            onSecondaryContainer = Color(0xFF1B3300),
            tertiary = Color(0xFF00695C),
            tertiaryContainer = Color(0xFFE0F2F1),
            onTertiaryContainer = Color(0xFF00201A),
            background = Color(0xFFF5FAF5),
            surface = Color(0xFFFFFFFF),
            surfaceVariant = Color(0xFFE8F5E9),
            onBackground = Color(0xFF0C1F0D),
            onSurface = Color(0xFF0C1F0D)
        ),
        darkColorScheme = darkColorScheme(
            primary = Color(0xFF81C784),
            onPrimary = Color(0xFF00390A),
            primaryContainer = Color(0xFF1B5E20),
            onPrimaryContainer = Color(0xFFE8F5E9),
            secondary = Color(0xFFAED581),
            secondaryContainer = Color(0xFF33691E),
            onSecondaryContainer = Color(0xFFF1F8E9),
            tertiary = Color(0xFF80CBC4),
            tertiaryContainer = Color(0xFF004D40),
            onTertiaryContainer = Color(0xFFE0F2F1),
            background = Color(0xFF0A140B),
            surface = Color(0xFF122113),
            surfaceVariant = Color(0xFF1A2E1C),
            onBackground = Color(0xFFE8F5E9),
            onSurface = Color(0xFFE8F5E9)
        )
    ),

    SUNSET_AMBER(
        id = "SUNSET_AMBER",
        nameZh = "落日琥珀",
        nameEn = "Sunset Amber",
        primaryColor = Color(0xFFE65100),
        surfaceColor = Color(0xFF23160C),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFFE65100),
            onPrimary = Color(0xFFFFFFFF),
            primaryContainer = Color(0xFFFFE0B2),
            onPrimaryContainer = Color(0xFF3E1A00),
            secondary = Color(0xFFD84315),
            secondaryContainer = Color(0xFFFFCCBC),
            onSecondaryContainer = Color(0xFF3B0B00),
            tertiary = Color(0xFFF57C00),
            tertiaryContainer = Color(0xFFFFE0B2),
            onTertiaryContainer = Color(0xFF3E1A00),
            background = Color(0xFFFFFBF7),
            surface = Color(0xFFFFFFFF),
            surfaceVariant = Color(0xFFFFF0E0),
            onBackground = Color(0xFF2A1505),
            onSurface = Color(0xFF2A1505)
        ),
        darkColorScheme = darkColorScheme(
            primary = Color(0xFFFFB74D),
            onPrimary = Color(0xFF4A1B00),
            primaryContainer = Color(0xFFE65100),
            onPrimaryContainer = Color(0xFFFFE0B2),
            secondary = Color(0xFFFF8A65),
            secondaryContainer = Color(0xFFBF360C),
            onSecondaryContainer = Color(0xFFFFCCBC),
            tertiary = Color(0xFFFFB74D),
            tertiaryContainer = Color(0xFFE65100),
            onTertiaryContainer = Color(0xFFFFE0B2),
            background = Color(0xFF170E07),
            surface = Color(0xFF23160D),
            surfaceVariant = Color(0xFF311F13),
            onBackground = Color(0xFFFFE0B2),
            onSurface = Color(0xFFFFE0B2)
        )
    ),

    INK_SLATE(
        id = "INK_SLATE",
        nameZh = "极简水墨",
        nameEn = "Monochrome Ink",
        primaryColor = Color(0xFF37474F),
        surfaceColor = Color(0xFF1E2428),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFF37474F),
            onPrimary = Color(0xFFFFFFFF),
            primaryContainer = Color(0xFFCFD8DC),
            onPrimaryContainer = Color(0xFF102027),
            secondary = Color(0xFF546E7A),
            secondaryContainer = Color(0xFFECEFF1),
            onSecondaryContainer = Color(0xFF102027),
            tertiary = Color(0xFF455A64),
            tertiaryContainer = Color(0xFFCFD8DC),
            onTertiaryContainer = Color(0xFF102027),
            background = Color(0xFFF8F9FA),
            surface = Color(0xFFFFFFFF),
            surfaceVariant = Color(0xFFECEFF1),
            onBackground = Color(0xFF1C2529),
            onSurface = Color(0xFF1C2529)
        ),
        darkColorScheme = darkColorScheme(
            primary = Color(0xFFB0BEC5),
            onPrimary = Color(0xFF102027),
            primaryContainer = Color(0xFF37474F),
            onPrimaryContainer = Color(0xFFECEFF1),
            secondary = Color(0xFF90A4AE),
            secondaryContainer = Color(0xFF263238),
            onSecondaryContainer = Color(0xFFECEFF1),
            tertiary = Color(0xFFB0BEC5),
            tertiaryContainer = Color(0xFF37474F),
            onTertiaryContainer = Color(0xFFECEFF1),
            background = Color(0xFF121619),
            surface = Color(0xFF1A1F23),
            surfaceVariant = Color(0xFF242C31),
            onBackground = Color(0xFFECEFF1),
            onSurface = Color(0xFFECEFF1)
        )
    );

    companion object {
        fun fromId(id: String): ThemePresetId {
            return entries.firstOrNull { it.id == id } ?: DEFAULT_VIOLET
        }
    }
}

enum class WallpaperPresetId(
    val id: String,
    val nameZh: String,
    val nameEn: String,
    val drawableRes: Int?
) {
    NONE(
        id = "NONE",
        nameZh = "无壁纸 (纯色)",
        nameEn = "Solid Color",
        drawableRes = null
    ),
    CYBER_GRID(
        id = "CYBER_GRID",
        nameZh = "赛博网格",
        nameEn = "Cyber Grid",
        drawableRes = R.drawable.img_wallpaper_cyber
    ),
    DEEP_OCEAN(
        id = "DEEP_OCEAN",
        nameZh = "深海浪花",
        nameEn = "Deep Ocean",
        drawableRes = R.drawable.img_wallpaper_ocean
    ),
    STARRY_COSMOS(
        id = "STARRY_COSMOS",
        nameZh = "璀璨星空",
        nameEn = "Starry Cosmos",
        drawableRes = R.drawable.img_wallpaper_cosmos
    );

    companion object {
        fun fromId(id: String): WallpaperPresetId {
            return entries.firstOrNull { it.id == id } ?: NONE
        }
    }
}
