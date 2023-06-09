# VoteKick

`Minecraft` 投票踢人插件

## 介绍
玩家可以在发起对其他玩家踢出服务器的投票，被踢出的玩家在指定时间内不能再登陆服务器


## 机制
### 踢出判断
默认情况下，投票数占总票数的一半以上(不含一半)则认定踢出玩家。

服主可以自定义这个比例，你也可以将比例设置为 `0` 然后设置 `kick-at-least`(至少赞成多少票) 固定值来实现不按比例而是按投票数达到多少就踢出玩家。

### IP 唯一投票
为了防止开小号投票有失公正，默认情况下，每个 IP 只能投 1 票。

服主可以自定义这个值, 设置为 `0` 则不限制。



## 命令:

| 命令                    | 功能       |
|-----------------------|----------|
| `vk create <玩家> [原因]` | 发起投票     |
| `vk yes`              | 赞成投票     |
| `vk no`               | 反对投票     |
| `vk info`             | 获取当前投票信息 |
| `vk cancel`           | 取消当前投票   |
| `vk unkick <玩家>`      | 取消踢出玩家   |


## 配置项
```yaml
# 配置文件版本
# 不要修改这个值!
version: 1

# 踢出的赞成比率
# 当 赞成 / (赞成 + 反对) 大于这个值时认为投票有效
kick-factor: 0.5

# 至少要达到多少票才踢出玩家
# 避免服务器人数少的情况下有失公正, 可以设置这个值
kick-at-least: 0

# 踢出玩家时间
# 单位: 秒
# 默认: 1800
kick-duration: 1800

# 投票持续时间
# 单位: 秒
# 默认: 180
vote-duration: 180

# 服务器发起投票冷却时间
# 服务器在冷却时间内任何人不得发起投票
# 单位: 秒
# 默认: 500
server-cd: 300

# 玩家独立的发起投票冷却时间
# 玩家在冷却时间内不得发起投票
# 单位: 秒
player-cd: 3600

# 每个 IP 最多发起多少个投票
# 如果不限制输入 0
max-votes-per-ip: 1

# 是否允许踢 OP
allow-kick-op: true

# 踢掉的 OP 是否允许重新登陆
allow-op-rejoin: true

# 默认投反对票
# 如果你的服务器允许玩家长时间挂机应当设置为 false, 否则挂机的玩家默认反对可能导致永远踢不掉人
# 同时, 如果这个值为 true, 那么 IP 限制是无效的, 因为所有人会默认投了票, 他们可以无视 IP 限制修改投票
default-disapprove: false

# 默认理由
default-reason: '该玩家太懒，居然没写理由'
```
