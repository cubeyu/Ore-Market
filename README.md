# Ore-Market
# Ore Market是Minecraft中一个逼真的矿石股票市场系统
# 你可以在Spigot上找到这个插件 -> https://www.spigotmc.org/resources/91015/
# 本插件由cubeyu维护

# 权限
权限:
  /openmarket 或 /om: oremarket.open # 打开OreMarket图形界面
  /om-reload: oremarket.reload       # 重新加载OreMarket配置
  /om-stats: oremarket.stats         # 查看服务器特定统计信息
  /om-crash: oremarket.crash         # 使市场崩溃

# 变量
占位符:
  [name]: 矿石名称
  [stock]: 矿石库存数量
  [value]: 矿石当前价值
  [cost]: 矿石当前成本
  [change]: 矿石价值变动（价值-成本）
  [percent]: 矿石价值变动百分比
  [balance]: 玩家余额
  [player]: 玩家名称

# Placeholder API（全场景可用）
# （无下载命令）
Placeholder API占位符:
  %oremarket_name_{item-slot}%: 矿石名称
  %oremarket_stock_{item-slot}%: 矿石库存数量
  %oremarket_value_{item-slot}%: 矿石当前价值
  %oremarket_cost_{item-slot}%: 矿石当前成本
  %oremarket_change_{item-slot}%: 矿石价值变动（价值-成本）
  %oremarket_percent_{item-slot}%: 矿石价值变动百分比

# 制作图形界面
模板物品:
  0:                                # 槽位
    item: DIAMOND                   # 物品材质
    name: '&b钻石矿石'              # 物品名称
    lore:                           # lore文本（可多行）
      - '&a价值: $[value]'          # 第1行
      - '&a原价: $[cost]'           # 第2行
      - '&a库存: [stock]'           # 第3行
      - '&a变动: [percent]%'        # 第4行
      - '&7右键点击购买'            # 第5行
      - '&7左键点击出售'            # 第6行
    cost: 1000                      # 矿石原始成本
    value: 1000                     # 矿石当前价值
    stock: 100                      # 库存数量

玩家头颅:
  1:                                # 槽位
    item: PLAYER_HEAD               # 物品材质
    head: '玩家名称'                # 头颅名称
    name: '&b头颅'                  # 物品名称
    flags:
      copymeta: true                # 必需（同时复制名称和lore）

物品标记:
  flags:
    copymeta: true   # 复制物品元数据（头颅必需）
    buyonly: true    # 只能购买此物品
    sellonly: true   # 只能出售此物品
    hide: true       # 物品在聊天消息中隐藏

物品命令:
  commands:
    - '[close]'  # 关闭图形界面
    - '[msg]'    # 向玩家发送消息
    - '/command' # 执行任意命令
