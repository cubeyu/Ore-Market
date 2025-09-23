<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ore-Market 文档</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        body {
            font-family: "Arial", "Microsoft YaHei", sans-serif;
            line-height: 1.8;
            color: #333;
            background-color: #f9f9f9;
            padding: 2rem 0;
        }
        .container {
            max-width: 1000px;
            margin: 0 auto;
            padding: 0 2rem;
            background-color: #fff;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.05);
            padding: 3rem 2.5rem;
        }
        h1 {
            font-size: 2.2rem;
            color: #2c3e50;
            margin-bottom: 1.5rem;
            border-bottom: 2px solid #3498db;
            padding-bottom: 0.8rem;
        }
        h2 {
            font-size: 1.8rem;
            color: #2c3e50;
            margin: 2.2rem 0 1.2rem;
            border-left: 4px solid #3498db;
            padding-left: 0.8rem;
        }
        h3 {
            font-size: 1.4rem;
            color: #34495e;
            margin: 1.8rem 0 1rem;
        }
        p {
            margin-bottom: 1.2rem;
            font-size: 1rem;
        }
        a {
            color: #3498db;
            text-decoration: none;
            transition: color 0.3s;
        }
        a:hover {
            color: #2980b9;
            text-decoration: underline;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin: 1.2rem 0 1.8rem;
            font-size: 0.95rem;
        }
        th, td {
            border: 1px solid #ddd;
            padding: 0.9rem 1.2rem;
            text-align: left;
        }
        th {
            background-color: #f5f7fa;
            color: #2c3e50;
            font-weight: 600;
        }
        tr:nth-child(even) {
            background-color: #fafbfc;
        }
        pre {
            background-color: #f8f9fa;
            border: 1px solid #e9ecef;
            border-radius: 6px;
            padding: 1.2rem;
            margin: 1rem 0 1.8rem;
            overflow-x: auto;
        }
        code {
            font-family: "Consolas", "Monaco", monospace;
            font-size: 0.9rem;
            color: #2d3748;
        }
        .note {
            color: #666;
            font-style: italic;
            margin: 0.5rem 0 1.5rem;
        }
    </style>
</head>
<body>
    <div class="container">
        <!-- 标题部分 -->
        <h1>Ore-Market</h1>
        <p>Ore Market 是 Minecraft 中一个逼真的矿石股票市场系统</p>
        <p>你可以在 Spigot 上找到该插件 → <a href="https://www.spigotmc.org/resources/91015/" target="_blank">https://www.spigotmc.org/resources/91015/</a></p>
        <p>本插件由 cubeyu 维护</p>

        <!-- 权限说明 -->
        <h2>权限说明</h2>
        <table>
            <thead>
                <tr>
                    <th>命令</th>
                    <th>权限节点</th>
                    <th>功能描述</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td><code>/openmarket</code> 或 <code>/om</code></td>
                    <td><code>oremarket.open</code></td>
                    <td>打开 OreMarket 图形界面</td>
                </tr>
                <tr>
                    <td><code>/om-reload</code></td>
                    <td><code>oremarket.reload</code></td>
                    <td>重新加载 OreMarket 配置文件</td>
                </tr>
                <tr>
                    <td><code>/om-stats</code></td>
                    <td><code>oremarket.stats</code></td>
                    <td>查看服务器特定统计数据</td>
                </tr>
                <tr>
                    <td><code>/om-crash</code></td>
                    <td><code>oremarket.crash</code></td>
                    <td>触发市场崩溃</td>
                </tr>
            </tbody>
        </table>

        <!-- 变量与占位符 -->
        <h2>变量与占位符</h2>

        <h3>基础占位符</h3>
        <table>
            <thead>
                <tr>
                    <th>占位符</th>
                    <th>描述</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td><code>[name]</code></td>
                    <td>矿石名称</td>
                </tr>
                <tr>
                    <td><code>[stock]</code></td>
                    <td>矿石库存数量</td>
                </tr>
                <tr>
                    <td><code>[value]</code></td>
                    <td>矿石当前价值</td>
                </tr>
                <tr>
                    <td><code>[cost]</code></td>
                    <td>矿石原始成本</td>
                </tr>
                <tr>
                    <td><code>[change]</code></td>
                    <td>矿石价值变动（当前价值 - 原始成本）</td>
                </tr>
                <tr>
                    <td><code>[percent]</code></td>
                    <td>矿石价值变动百分比</td>
                </tr>
                <tr>
                    <td><code>[balance]</code></td>
                    <td>玩家余额</td>
                </tr>
                <tr>
                    <td><code>[player]</code></td>
                    <td>玩家名称</td>
                </tr>
            </tbody>
        </table>

        <h3>Placeholder API（全场景可用）</h3>
        <p class="note">（无需额外下载命令）</p>
        <table>
            <thead>
                <tr>
                    <th>占位符</th>
                    <th>描述</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td><code>%oremarket_name_{item-slot}%</code></td>
                    <td>矿石名称</td>
                </tr>
                <tr>
                    <td><code>%oremarket_stock_{item-slot}%</code></td>
                    <td>矿石库存数量</td>
                </tr>
                <tr>
                    <td><code>%oremarket_value_{item-slot}%</code></td>
                    <td>矿石当前价值</td>
                </tr>
                <tr>
                    <td><code>%oremarket_cost_{item-slot}%</code></td>
                    <td>矿石原始成本</td>
                </tr>
                <tr>
                    <td><code>%oremarket_change_{item-slot}%</code></td>
                    <td>矿石价值变动（当前价值 - 原始成本）</td>
                </tr>
                <tr>
                    <td><code>%oremarket_percent_{item-slot}%</code></td>
                    <td>矿石价值变动百分比</td>
                </tr>
            </tbody>
        </table>

        <!-- 图形界面（GUI）配置 -->
        <h2>图形界面（GUI）配置</h2>

        <h3>模板物品配置</h3>
        <pre><code class="language-yaml">0:                                # 槽位
  item: DIAMOND                   # 物品材质
  name: '&b钻石矿石'              # 物品名称
  lore:                           # 描述文本（可多行）
    - '&a价值: $[value]'          # 第1行
    - '&a原价: $[cost]'           # 第2行
    - '&a库存: [stock]'           # 第3行
    - '&a变动: [percent]%'        # 第4行
    - '&7右键点击购买'            # 第5行
    - '&7左键点击出售'            # 第6行
  cost: 1000                      # 矿石原始成本
  value: 1000                     # 矿石当前价值
  stock: 100                      # 库存数量</code></pre>

        <h3>玩家头颅配置</h3>
        <pre><code class="language-yaml">1:                                # 槽位
  item: PLAYER_HEAD               # 物品材质
  head: '玩家名称'                # 头颅对应的玩家名
  name: '&b头颅'                  # 物品名称
  flags:
    copymeta: true                # 必需（同时复制名称和描述文本）</code></pre>

        <h3>物品标记配置</h3>
        <pre><code class="language-yaml">flags:
  copymeta: true   # 复制物品元数据（头颅配置必需）
  buyonly: true    # 仅允许购买此物品
  sellonly: true   # 仅允许出售此物品
  hide: true       # 物品在聊天消息中隐藏</code></pre>

        <h3>物品命令配置</h3>
        <pre><code class="language-yaml">commands:
  - '[close]'  # 关闭图形界面
  - '[msg]'    # 向玩家发送消息
  - '/command' # 执行任意命令</code></pre>
    </div>
</body>
</html>
