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

- 关于房间主人的信息是一个object，包括`{username: <用户名>, badge: <头像>}` 。
- 分时间段的统计数据是一个object，根据类别分类，每一类是一个array，统计每个小时间段内的数据（相当于在游戏内overview里看到的那些小圆点）。
- 在统计时间段的总数据是一个object，包括这个房间在请求的interval里面的总数据。

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

今天懒了

### 6. Console


### 7. Memory


### 8. 对游戏对象的操作
#### Add Intent
包括删除flag，自杀Creep，删除ConstructionSite，unclaim controller，删除Structure (不确定其他intent是否可以)

#### ...




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

