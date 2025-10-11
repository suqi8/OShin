package com.suqi8.oshin.hook.phone

import kotlin.math.abs

/**
 * 一个核心工具对象，封装了所有从短信中解析验证码和公司名的复杂逻辑。
 */
object SmsCodeUtils {

    // --- 定义验证码的匹配优先级 ---
    private const val LEVEL_DIGITAL_6 = 4     // 6位纯数字，最优先
    private const val LEVEL_DIGITAL_4 = 3     // 4位纯数字
    private const val LEVEL_DIGITAL_OTHERS = 2// 其他长度的纯数字
    private const val LEVEL_TEXT = 1          // 数字+字母 混合
    private const val LEVEL_CHARACTER = 0     // 纯字母，最末
    private const val LEVEL_NONE = -1

    private val CHINESE_REGEX = "[\u4e00-\u9fa5]|。".toRegex()
    private val WHITESPACE_REGEX = "\\s*".toRegex()
    private val COMPANY_REGEX = "((?<=【)(.*?)(?=】))|((?<=\\[)(.*?)(?=]))".toRegex()

    /**
     * 解析验证码的总入口。它会先尝试自定义规则，如果失败再使用默认规则。
     */
    @JvmStatic
    fun parseSmsCodeIfExists(content: String): String {
        return parseByDefaultRule(content)
    }

    /**
     * 从短信内容中解析出公司或应用名（通常在【】或[]中）。
     */
    fun parseCompany(content: String): String {
        return COMPANY_REGEX.findAll(content)
            .map { it.value }
            .joinToString(" ")
    }

    /**
     * 使用通用的默认规则来解析验证码。
     */
    private fun parseByDefaultRule(content: String, ): String {
        // 加载用于匹配的“关键字”列表（如“验证码”、“code”等）
        val keywordsRegex = smsRule
        // 在短信中找到第一个匹配的关键字，如果找不到则直接返回空字符串
        val keyword = keywordsRegex.toRegex().find(content)?.value ?: return ""

        // 根据短信是否包含中文，调用不同的处理方法
        return if (content.contains(CHINESE_REGEX)) {
            getSmsCodeCN(keyword, content)
        } else {
            getSmsCodeEN(keyword, content)
        }
    }

    /**
     * 处理中文短信，验证码可能是字母和数字的组合。
     */
    private fun getSmsCodeCN(keyword: String, content: String): String {
        val codeRegex = "(?<![a-zA-Z0-9])[a-zA-Z0-9]{4,8}(?![a-zA-Z0-9])".toRegex()
        // 先去掉所有空白字符尝试解析一次，如果没解析出来，再用原始文本解析一次
        return getSmsCode(codeRegex, keyword, content.replace(WHITESPACE_REGEX, ""))
            .takeIf { it.isNotEmpty() }
            ?: getSmsCode(codeRegex, keyword, content)
    }

    /**
     * 处理英文短信，通常验证码是纯数字。
     */
    private fun getSmsCodeEN(keyword: String, content: String): String {
        val codeRegex = "(?<![0-9])[0-9]{4,8}(?![0-9])".toRegex()
        // 先用原始文本解析一次，如果没解析出来，再去掉空白字符尝试一次
        return getSmsCode(codeRegex, keyword, content)
            .takeIf { it.isNotEmpty() }
            ?: getSmsCode(codeRegex, keyword, content.replace(WHITESPACE_REGEX, ""))
    }

    /**
     * 最核心的智能解析方法，通过一系列启发式规则找到最佳验证码。
     */
    private fun getSmsCode(codeRegex: Regex, keyword: String, content: String): String {
        // 1. 找出短信中所有可能是验证码的字符串
        val possibleCodes = codeRegex.findAll(content).map { it.value }.toList()
        if (possibleCodes.isEmpty()) return ""

        // 2. 筛选出离“关键字”30个字符内的候选码，如果没有则考虑所有
        val filteredCodes = possibleCodes.filter { code ->
            distanceToKeyword(keyword, code, content) <= 30
        }.ifEmpty { possibleCodes }


        // 3. 根据格式和距离，给每个候选码打分，选出最优的那个
        var bestCode = ""
        var maxMatchLevel = LEVEL_NONE
        var minDistance = content.length

        for (code in filteredCodes) {
            val currentLevel = getMatchLevel(code)
            if (currentLevel > maxMatchLevel) {
                maxMatchLevel = currentLevel
                minDistance = distanceToKeyword(keyword, code, content)
                bestCode = code
            } else if (currentLevel == maxMatchLevel) {
                val currentDistance = distanceToKeyword(keyword, code, content)
                if (currentDistance < minDistance) {
                    minDistance = currentDistance
                    bestCode = code
                }
            }
        }
        return bestCode
    }

    /**
     * 根据字符串格式，判断其作为验证码的“匹配等级”或“优先级”。
     */
    private fun getMatchLevel(matchedStr: String): Int = when {
        matchedStr.matches("^[0-9]{6}$".toRegex()) -> LEVEL_DIGITAL_6
        matchedStr.matches("^[0-9]{4}$".toRegex()) -> LEVEL_DIGITAL_4
        matchedStr.matches("^[0-9]*$".toRegex()) -> LEVEL_DIGITAL_OTHERS
        matchedStr.matches("^[a-zA-Z]*$".toRegex()) -> LEVEL_CHARACTER
        else -> LEVEL_TEXT
    }

    /**
     * 计算候选码和关键字之间的距离。
     */
    private fun distanceToKeyword(keyword: String, possibleCode: String, content: String): Int {
        val keywordIdx = content.indexOf(keyword)
        val possibleCodeIdx = content.indexOf(possibleCode)
        return abs(keywordIdx - possibleCodeIdx)
    }
}
