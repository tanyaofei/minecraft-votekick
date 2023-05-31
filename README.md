# VoteKick

## 命令:

_`true`_: 表示所有人能执行

_`op`_: 表示只有 `op` 能执行

| 命令                         | 功能              | 权限        | 默认权限   |
|----------------------------|-----------------|-----------|--------|
| /vk create `<玩家名称>` `[原因]` | 发起一轮将玩家踢出服务器的投票 | vk.create | `true` |
| /vk cancel                 | 取消本轮投票          | vk.cancel | `op`   |
| /vk yes                    | 投赞成票            | vk.vote   | `true` |
| /vk no                     | 投反对票            | vk.vote   | `true` |
| /vk info                   | 获取本轮投票信息        | vk.info   | `true` |
| /vk unkick `<玩家名称>`        | 取消踢出玩家          | vk.info   | `true` |
| /vk help                   | 获取命令帮助信息        | vk.help   | `true` |
| /vk reload                 | 重新加载配置文件        | vk.reload | `op`   |

## 配置文件与语言文件
引入插件后第一次启动会创建 `plugin/votekick/config.yml` 文件，详情请看此文件