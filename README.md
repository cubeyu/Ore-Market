Ore-Market
Ore Market 是 Minecraft 中一个逼真的矿石股票市场系统你可以在 Spigot 上找到该插件 → https://www.spigotmc.org/resources/91015/本插件由 cubeyu 维护
权限说明
命令	权限节点	功能描述
/openmarket 或 /om	oremarket.open	打开 OreMarket 图形界面
/om-reload	oremarket.reload	重新加载 OreMarket 配置文件
/om-stats	oremarket.stats	查看服务器特定统计数据
/om-crash	oremarket.crash	触发市场崩溃
变量与占位符
基础占位符
占位符	描述
[name]	矿石名称
[stock]	矿石库存数量
[value]	矿石当前价值
[cost]	矿石原始成本
[change]	矿石价值变动（当前价值 - 原始成本）
[percent]	矿石价值变动百分比
[balance]	玩家余额
[player]	玩家名称
Placeholder API（全场景可用）
（无需额外下载命令）
占位符	描述
%oremarket_name_{item-slot}%	矿石名称
%oremarket_stock_{item-slot}%	矿石库存数量
%oremarket_value_{item-slot}%	矿石当前价值
%oremarket_cost_{item-slot}%	矿石原始成本
%oremarket_change_{item-slot}%	矿石价值变动（当前价值 - 原始成本）
%oremarket_percent_{item-slot}%	矿石价值变动百分比
图形界面（GUI）配置
模板物品配置
yaml
0:                                # 槽位
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
  stock: 100                      # 库存数量
玩家头颅配置
yaml
1:                                # 槽位
  item: PLAYER_HEAD               # 物品材质
  head: '玩家名称'                # 头颅对应的玩家名
  name: '&b头颅'                  # 物品名称
  flags:
    copymeta: true                # 必需（同时复制名称和描述文本）
物品标记配置
yaml
flags:
  copymeta: true   # 复制物品元数据（头颅配置必需）
  buyonly: true    # 仅允许购买此物品
  sellonly: true   # 仅允许出售此物品
  hide: true       # 物品在聊天消息中隐藏
物品命令配置
yaml
commands:
  - '[close]'  # 关闭图形界面
  - '[msg]'    # 向玩家发送消息
  - '/command' # 执行任意命令
