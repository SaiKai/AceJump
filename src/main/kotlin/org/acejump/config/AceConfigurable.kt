package org.acejump.config

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.options.Configurable
import org.acejump.config.AceConfig.Companion.bigrams
import org.acejump.config.AceConfig.Companion.settings

class AceConfigurable: Configurable {
  private val logger = Logger.getInstance(AceConfigurable::class.java)

  private val panel by lazy { AceSettingsPanel() }

  override fun getDisplayName() = "AceJump"

  override fun createComponent() = panel.rootPanel

  override fun isModified() =
    panel.allowedChars != settings.allowedChars ||
      panel.keyboardLayout != settings.layout ||
      panel.jumpModeColor != settings.jumpModeColor ||
      panel.targetModeColor != settings.targetModeColor ||
      panel.definitionModeColor != settings.definitionModeColor ||
      panel.textHighlightColor != settings.textHighlightColor ||
      panel.tagForegroundColor != settings.tagForegroundColor ||
      panel.tagBackgroundColor != settings.tagBackgroundColor ||
      panel.supportPinyin != settings.supportPinyin

  private fun String.distinctAlphanumerics() =
    if (isEmpty()) settings.layout.text
    else toList().distinct().filter(Char::isLetterOrDigit).joinToString("")

  override fun apply() {
    panel.allowedChars.distinctAlphanumerics().let {
      settings.allowedChars = it
      AceConfig.allPossibleTags = it.bigrams()
    }

    settings.layout = panel.keyboardLayout
    panel.jumpModeColor ?.let { settings.jumpModeColor = it }
    panel.targetModeColor ?.let { settings.targetModeColor = it }
    panel.definitionModeColor ?.let { settings.definitionModeColor = it }
    panel.textHighlightColor ?.let { settings.textHighlightColor = it }
    panel.tagForegroundColor ?.let { settings.tagForegroundColor = it }
    panel.tagBackgroundColor ?.let { settings.tagBackgroundColor = it }
    settings.supportPinyin = panel.supportPinyin

    logger.info("User applied new settings: $settings")
  }

  override fun reset() = panel.reset(settings)
}