package com.leroymerlin.commit;

/**
 * From https://github.com/commitizen/conventional-commit-types
 *
 * @author Damien Arrachequesne
 */
public enum ChangeType {

    FEAT("Features", "新功能"),
    FIX("Bug Fixes", "错误修复"),
    DOCS("Documentation", "仅文档更改"),
    STYLE("Styles", "不影响代码含义的更改（空白，格式，缺少分号等）"),
    REFACTOR("Code Refactoring", "既不修正错误也不增加功能的代码更改"),
    PERF("Performance Improvements", "改进性能的代码更改"),
    TEST("Tests", "添加缺失或更正现有测试"),
    BUILD("Builds", "构建系统相关文件修改，例如构建脚本相关"),
    CHORE("Chores", "更改构建过程或辅助工具和库"),
    REVERT("Revert", "还原代码");

    public final String title;
    public final String description;

    ChangeType(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String label() {
        return this.name().toLowerCase();
    }

    @Override
    public String toString() {
        return String.format("%s - %s", this.label(), this.description);
    }
}
