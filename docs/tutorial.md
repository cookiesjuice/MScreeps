# Screeps周边教程
<br>
这篇文章我会介绍写Screeps周边的相关知识，包括介绍各种资源和API。
先简单介绍一下和服务器交换信息的方法。包括HTTP请求和websocket。

在代码块中间会有一些例如`<你的数据>`，这里把尖括号和内容替换成实际的数据就可以了，不需要留下括号。例如 `我爱 <我最喜欢的游戏>` ，替换成 `我爱 Screeps` 就可以了。用方括号括起来的参数可以省略，比如`我<写代码方式>[并且<玩游戏方式>]`,既可以是`我hardcode并且手操`，也可以是`我hardcode`。

## HTTP请求
http请求的具体代码根据不同语言不同，这里不介绍具体写法了，只是列出所有的endpoint，请求方法，和返回信息的格式
### 1. 身份验证
除了地形和积分榜之外的大多数请求都需要有身份验证。第一种是使用用户名和密码。
#### 使用用户名和密码认证
如果使用用户名和密码，需要先发送一个POST请求到

- [POST] `https://screeps.com/api/auth/signin`

**发送的数据**： `{ email: <你的用户名>, password: <你的密码>}`

**返回的数据**： `{ok: 1, token: <一个长度是40的16进制字符串>}`

你需要把这个`token`保存下来，并且每次发送请求的时候都把这个token放到请求的header里： `{X-Token: <保存的token>, X-Username: <保存的token>}`

如果请求返回的**header**（注意不是正文）里包含`X-Token`，就把返回的`X-Token`保存下来替换以前的token。

#### 使用Auth Token认证
在用户设置 <https://screeps.com/a/#!/account/auth-tokens> 里可以创建一个Auth Token用于认证。Auth Token的认证方法在官网文档里也有介绍 <https://docs.screeps.com/auth-tokens.html> 。只需要把auth token放到请求的header里就可以了`{X-Token: <你的auth token>}`。注意与用户名和密码验证不同，这个token会受到请求数量的限制，而且在请求的时候不会返回新的token。

### 1 房间
#### Room Overview
这个是对应我们在游戏客户端里看到的房间总览，包括用户信息，一定时间内用于升级，建造，和采集到的能量，生产和死亡的Creep数量。

- [GET] `https://screeps.com/api/game/room-overview?interval=<interval>&room=<roomName>&shard=<shardName>`

参数说明：

- interval: 可以是8，180，或者1440，分别对应1小时，24小时和7天。

- roomName: 你的房间名

- shardName: 服务器的shard名（比如`shard3`）。如果对应的服务器不支持shard，省略这个参数。

**返回的数据**：

```
{
  "ok":1,
  "owner":<关于房间主人的信息>
  "stats":<分时间段的统计数据>,
  "statsMax":<我暂时也不清楚是干啥的数据>,
  "totals": <在统计时间段的总数据>
}
```

- 关于房间主人的信息是一个object，包括`{username: <
- 名>, badge: <头像>}` 。
- 分时间段的统计数据是一个object，根据类别分类，每一类是一个array，统计每个小时间段内的数据（相当于在游戏内overview里看到的那些小圆点）。
- 在统计时间段的总数据是一个object，包括这个房间在请求的interval里面的总数据。

#### World size
获取世界地图的大小
- [GET] `https://screeps.com/api/game/world-size?shard=<shardName>`
**返回的数据**：
```
{
  ok: 1,
  width: <宽度>,
  height: <高度>
}
```


#### Room Terrain
用于获取房间的地形。注意这个请求不需要验证，所以不会在header返回X-Token

- [GET] `https://screeps.com/api/game/room-terrain?room=<roomName>&shard=<shardName>[&encoded=1]`

参数说明：
如果encoded=1，会返回不同的数据格式

**返回的数据**：


```
{
  ok: 1,
  terrain: <地形信息>
}
```

如果没有encoded：
- 地形信息是一个array，其中每个元素是地图上一个点的地形，是object：`{room: <房间名>, x: <x坐标>, y: <y坐标>, type: <地形>}`。
其中地形可以是`wall`或者`swamp`。如果是`plain`就不会返回这个坐标。

如果有encoded=1：
- 地形信息是只有一个元素的array，这个元素是一个object: `{_id: <地形对象的id>, room: <房间名>, terrain: <很长的一串数字>, type: terrain}`。这个很长的一串数字代表从左往右，从上往下的地形信息，每个数字代表一格的地形。0是`plain`，2是`swamp`，1和3是`wall`。

#### Room History
查看一个房间的历史

- [GET] `https://screeps.com/room-history/<shardName>/<roomName>/<tick>.json`

- tick应该在 `Game.time - 50` 到 `Game.time - 500000` 之间，并且是20的倍数。返回的信息会包括接下来20ticks的数据。

**返回的数据**：

有点复杂，下次写

### 3. 市场
#### Market Order Index
相当于游戏里面的market首页信息，获取每种资源的order的概况

- [GET] `https://screeps.com/api/game/market/orders-index?shard=<shardName>`

**返回的数据**：
```
{
  ok: 1,
  list: <一堆信息>
}
```
- 一堆信息是一个array，里面每个元素是 `{_id: <商品名称>, count: <总量>, avgPrice: <平均价格>, stddevPrice: <价格的标准差>}`

#### My Orders
获取我的所有市场买卖单
- [GET] `https://screeps.com/api/game/market/my-orders`

**返回的数据**：
```
{
  ok: 1,
  shards: {
    shard0: <shard0的单>,
    shard1: <shard1的单>,
    shard2: <shard2的单>,
    shard3: <shard3的单>,
    intershard: <例如token之类的跨shard单>
  }
}
```

- 每个单的格式是一个array，其中每个元素是 `{_id: <orderId>, type: <sell或者buy>, amount: <order的总量>, remainingAmount: <order剩余的量>, roomName: <order的房间>}`

#### Orders
获取指定资源的全部买卖单
- [GET] `https://screeps.com/api/game/market/orders?resourceType=<resourceType>&shard=<shardName>`

**返回的数据**：
```
{
  ok: 1,
  list: <买卖单数组>
}
```
- 这个买卖单数组的格式和上面My Orders里的一样。

#### Money History
虽然是用户数据，但是因为在客户端里和市场放在一起，所以也放在这里了
显示用户的交易记录
- [GET] `https://screeps.com/api/user/money-history[?page=<pageNumber>]`

- 如果没有提供page，默认是page=0

**返回的数据**：
```
{
  ok: 1,
  page: <pageNumber>,
  list: <交易记录的数组>，
  hasMore: <是否有更多页>
}
```

交易记录的数组是一个array，每个元素是
```
{
  _id: <交易的id>,
  date: <交易的时间>,
  tick: <交易的游戏时间>,
  user: <deal的user id>,  // (也有可能是拿钱的人的id， 没有确认)
  type: <market.sell或者market.buy>,
  balance: <交易之后的cr余额>,
  change: <交易产生的cr金额变化>,
  market: <当时这个买卖单的详情>,
  shard: <发生交易的shard>
}
```

这个买卖单的详情是一个object：
```
{
  resourceType: <资源类型>,
  roomName: <订单的房间>,
  targetRoomName: <deal的人的房间>,
  price: <价格>,
  npc: <是否是NPC的买卖单>,
  owner: <单子主人的id。如果是npc，为null>,
  dealer: <deal的人的id>,
  amount: <交易的资源量>,
}
```

### 4. 信息
游戏里的邮件系统
#### Index
点开邮件系统里面首页的内容。有所有邮件的最基本信息和最后一条邮件的记录
- [GET] `https://screeps.com/api/user/messages/index`

**返回的数据**：
```
{
  ok: 1,
  messages: <所有聊天记录的数组>,
  users: <出现在聊天记录里的信息>
}
```

其中messages是一个array，每个元素是一个object：
```
{
  _id: <对方的user id>,
  message: {  // 这是你们最后的一条消息
    _id: <这条消息的id>,
    user: <收件人的user id （可能是对方也可能是自己）>,
	respondent: <对方的user id>,
    date: <信息的真实日期时间>
    type: <如果是对方发出，是in。否则是out>,
    text: <消息的文本>,
    unread: <是否未读 true/false>
  }
}
```

users是一个object，其中的格式是
```
{
  <每个用户的id>:{
    _id: <他的user id>,
    username: <他的username>,
    badge: <他的头像>
  }
}
```

#### Message List
读取和另一个人的聊天记录
- [GET] `https://screeps.com/api/user/messages/list?respondent=<对方的user id>`
- 注意对方的user id应该是一个16进制的数字的字符串，而不是对方的用户名。关于如何通过用户名搜索user id下面在用户信息的部分会讲到

**返回的数据**:
```
{
  ok: 1,
  messages: <聊天记录>
}
```
聊天记录是一个array，其中每个元素是
```
{
  _id: <这条消息的id>,
  date: <消息的真实日期时间>,
  type: <收到的消息为in，否则为out>,
  text: <消息的文本>,
  unread: <是否未读 true/false>
}
```

#### 未读邮件数

- [GET] `https://screeps.com/api/user/messages/unread-count`

**返回的数据**：
```
{
  ok: 1,
  count: <未读的消息数>
}
```

#### 发送邮件
- [POST] `https://screeps.com/api/user/messages/send`

**发送的数据**：
`{respondent: <对方的user id>, text: <消息的文本>}`
- 注意对方的user id应该是一个16进制的数字的字符串，而不是对方的用户名。关于如何通过用户名搜索user id下面在用户信息的部分会讲到

接受的数据： `{ok: 1}`

### 5. 用户信息

#### 自己的账户信息
- [GET] `https://screeps.com/api/auth/me`

**返回的数据**:
```
{
  ok: 1,
  _id: <你自己的id>,
  email: <你注册的email地址>,
  username: <你的用户名>,
  cpu: <你的cpu上限>,
  badge: <你的头像>,
  password: <如果用auth token登陆为false，用户名密码登陆为true>,
  lastRespawnDate: <上一次respawn的时间>,
  notifyPrefs: <邮件通知的设置>,
  gcl: <总的GCL能量>,
  credit: <我也不知道是什么>,
  promoPeriodUntil: <应该是sub过期的时间>,
  money: <你的credit>,
  subscriptionTokens: <你账户里的subscription token数量>,
  cpuShard: <你每个shard分配的CPU>,
  cpuShardUpdatedTime: <你上次更新CPU shard的时间>,
  runtime: <我也不知道>,
  powerExperimentations: <剩下的power experimentation次数>,
  powerExperimentationsTime: <应该是experimentation结束的时间>,
  github: <你绑定的github账户和设置>,
  steam: <你绑定的steam账户和设置>,
  twitter: <你绑定的twitter账户和设置（虽然我不知道在哪里绑定）>
}
```
注意这个请求不会返回新的X-Token（返回的X-Token是空字符串）

#### 查询用户信息
- [GET] `https://screeps.com/api/user/find?[username=<username>][id=<userid>]`
- 注意 `username` 和 `id` 选择一个参数用于查询

**返回的数据**:
```
{
  ok:
  user:{
    _id: <对方的id>,
    username: <对方的用户名>,
    badge: <对方的头像>
  }
}
```
这个请求不需要登陆验证，也不会返回X-Token。

这里得到的id就是很多请求中使用的用户id。

#### 用户头像
- [GET] `https://screeps.com/api/user/badge-svg?username=<userName>`

**返回的数据**：
用户头像的svg文件

#### 查询用户房间
- [GET] `https://screeps.com/api/user/rooms?id=<用户的id>`

**返回的数据**:
```
{
  ok: 1,
  shards: {
    shard0: <shard0的房间名array>,
    shard1: <shard1的房间名array>,
    ...
  }
}
```
这个请求不需要登陆验证，也不会返回X-Token


#### 用户总览
获得自己的总览界面
- [GET] `https://screeps.com/api/user/overview?interval=<interval>&statName=<statName>`
- interval是8，180或者1440，分别代表1小时，24小时和7天。
- statName可以是`creepsLost`, `creepsProduced`, `energyConstruction`, `energyControl`, `energyCreeps`, `energyHarvested`, `powerProduced`

**返回的数据**:
```
{
  ok: 1,
  statsMax: <我也不知道,可能是全服务器最大的数据用来决定画圈的大小>,
  totals: {<所有的统计数据>},
  shards: {
    <shardName>: {
       rooms: <你的所有房间>,
       stats: <每个房间的数据>,
       gametimes: <我也不知道是什么>
    }
  },
}
```

#### 房间的地图信息
- [POST] `https://screeps.com/api/game/map-stats`

**发送的数据**：
```
{
  rooms: [<房间名的数组>],
  shard: <shardName>,
  statName: <statName0>
}
```
- 注意这里需要的`statName`与之前的房间总览不一样，这里的参数除了之前的数据，还可以是: `none0`, `owner0`, `minerals0`, `power0`

**返回的数据**：
```
{
  ok: 1,
  gameTime: <gameTime>,
  stats: <一个以房间名为key的对象，储存了你查询的数据，房间状态，和房间签名>,
  users: <一个以用户id为key的对象，储存了用户名和头像数据>
}
```

#### 杂项
这些可能用的比较少，我就放在一起，也不具体介绍，如果有需要自己试试就知道了。
- [GET] `https://screeps.com/api/user/respawn-prohibited-rooms`
- [GET] `https://screeps.com/api/user/world-status`
- [GET] `https://screeps.com/api/user/world-start-room`
- [GET] `https://screeps.com/api/xsolla/user`


### 6. Console
HTTP指令只能发送到console的命令，不能接收。接收需要通过websocket，会在之后介绍。
- [POST] `https://screeps.com/api/user/console`

**发送的数据**：
```
{
  expression: <指令>，
  shard: <shardName>
}
```

**接收的数据**：
```
{
  ok: 1,
  result: {
    ok: 1,
    n: <我也不知道是什么，但是通常是1>
  }
  ops: [
    {
      user: <user id>,
      expression: <你发送的指令>,
      shard: <shardName>,
      _id: <指令的id?>
    }
  ],
  insertedCount: <??>,
  insertedIds: [<??>]
}
```
接收的数据似乎也没什么用，只能用来确认自己发送成功。


### 7. Memory
#### 读Memory
- [GET] `https://screeps.com/api/user/memory?shard=<shardName>[&path=<要访问的Memory路径>]`
- 访问的路径是Memory之后的，比如rooms.E1N1

**返回的数据**：
```
{
  ok: 1,
  data:<Memory的内容>
}
```
这个内容是以 `gz:` 开头的，base64编码之后的gzip之后的Memory的数据。要重新变回JSON需要先base64解码然后gzip解压。

#### 写Memory
-[POST] `https://screeps.com/api/user/memory`
**发送的数据**：
```
{
  path: <要修改的Memory的路径>,
  value: <修改之后的值>,
  shard: <shardName>
}
```
- 如果没有path参数，会修改整个Memory
- 如果没有value参数，会清空这一部分Memory
- 所以这两个参数都没有就是清空整个Memory

**接收的数据**：
和console的接受数据基本上一样，只是`ops`当中的 `_id` 变成 `hidden` 。可能修改Memory相当于一个console指令。

#### 读Segment
- [GET] `https://screeps.com/api/user/memory-segment?segment=<segment编号(0-99)>&shard=<shardName>`
**接收的数据**：
格式和Memory完全相同

#### 写segment
- [POST] `https://screeps.com/api/user/memory-segment`

**发送的数据**：
```
{
  segment: <segment编号(0-99)>,
  data: <要写的数据>,
  shard: <shardName>
}
```
**接收的数据**:
```
{
  ok: 1
}
```

### 8. 对游戏对象的操作
#### Add Intent
包括删除flag，自杀Creep，删除ConstructionSite，unclaim controller，删除Structure (不确定其他intent是否可以)
都下次再写了。

#### ...

#### 新PC
- POST `https://screeps.com/api/game/power-creeps/create`

#### 升级PC
- POST `https://screeps.com/api/game/power-creeps/upgrade`

**发送的数据**：
```
{
  id: <pcid(不同于游戏内的object id)>,
  powers: {
    <POWER_ID>: <POWER_LEVEL>
  }
}
```

**接受的数据**：
没试过



### 9. 积分榜
我大多数时候写周边不在乎这个，所以先空着了，如果有需要再填坑


### 10. PVP
还没有研究过，不能给出任何细节信息

- [GET] `https://screeps.com/api/experimental/pvp?[interval=<>][start=<>]`


## Websocket
正在施工，敬请期待

## 参考资料
- <https://docs.screeps.com/auth-tokens.html>
- <https://github.com/screepers/python-screeps>
- <https://gist.github.com/bzy-xyz/9c4d8c9f9498a2d7983d>
- <https://github.com/daboross/rust-screeps-api>

如果有问题也可以问slack的 **#client-dev** 和 **#client-abuse** 频道

