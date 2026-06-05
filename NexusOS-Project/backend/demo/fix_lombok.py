import re, os

SRC_DIR = r"C:\Users\17270\IdeaProjects\demo\src\main\java"

def remove_method(content, method_sig):
    """删除从 method_sig 开始到匹配的右花括号的内容"""
    idx = content.find(method_sig)
    if idx == -1:
        return content
    brace_start = content.find('{', idx)
    if brace_start == -1:
        return content
    depth = 1
    pos = brace_start + 1
    while pos < len(content) and depth > 0:
        if content[pos] == '{':
            depth += 1
        elif content[pos] == '}':
            depth -= 1
        pos += 1
    method_end = pos
    # 往前找方法签名前的非空行
    method_start = idx
    while method_start > 0 and content[method_start - 1] in ' \t':
        method_start -= 1
    while method_start > 0 and content[method_start - 1] == '\n':
        method_start -= 1
    content = content[:method_start] + '\n' + content[method_end:]
    # 清理多余空行
    content = re.sub(r'\n{4,}', '\n\n\n', content)
    return content

files = []
for root, dirs, fnames in os.walk(SRC_DIR):
    for f in fnames:
        if f.endswith(".java"):
            fp = os.path.join(root, f)
            with open(fp, "r", encoding="utf-8") as fh:
                c = fh.read()
            if "public boolean equals" in c:
                files.append(fp)

print(f"找到 {len(files)} 个文件需要修复")

for fp in files:
    with open(fp, "r", encoding="utf-8") as fh:
        content = fh.read()
    original_len = len(content)

    # 删除 canEqual 方法
    content = re.sub(
        r'   protected boolean canEqual\(Object other\)\s*\{[^}]*\}\s*',
        '',
        content
    )

    # 删除 equals / hashCode / toString
    for sig in [
        "   public boolean equals(Object o) {",
        "   public boolean equals(Object obj) {",
        "   public int hashCode() {",
        '   public String toString() {',
    ]:
        content = remove_method(content, sig)

    content = re.sub(r'\n{4,}', '\n\n\n', content)

    if len(content) < original_len:
        with open(fp, "w", encoding="utf-8") as fh:
            fh.write(content)
        print(f"  已修复: {os.path.basename(fp)}")
    else:
        print(f"  跳过: {os.path.basename(fp)}")

print("\n完成！现在用 mvn compile 测试编译")
