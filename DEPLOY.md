# 发版规范

> 版本号使用类GNU的3位版本号

## 示例

发布`snapshot`版本。比如，主版本号最大的`1.x`分支的下一个版本被管理员置为`1.2.0-SNAPSHOT`，开发者增加了一个`feature`实现，这个`commit`被认为达到发版的要求，在本地可以参考执行：

```shell
git pull

# 切换到1.x分支
git checkout 1.x

# 修改pom版本
编辑项目根目录下的 pom.xml 文件中的 `<properties>` 下的 `<revision>x.x.x.RELEASE</revision>` 标签中版本信息
修改为最新 RELEASE版本加1，并将后缀变为 `SNAPSHOT`（如当前最新版本为 `1.2.8.RELEASE` 则修改为 `1.2.9-SNAPSHOT`）

# 提交最后一次的改动，这里的 commit 内容为：当前的snapshot版本号 + 变更内容
git add pom.xml && git commit -m '1.2.9-SNAPSHOT'
 
# 主版本分支打tag
git tag 1.2.9-SNAPSHOT

# 推送tag到git服务器
git push $ORIGIN 1.2.9-SNAPSHOT

# 发版
mvn clean deploy -D revision=1.2.9-SNAPSHOT -P deploy
```

发布`release`版本。当上个示例的`1.2.9-SNAPSHOT`版本发布之后，陆续又有几个`feature`添加了进来且发布了`SNAPSHOT`版本，考虑将这些`feature`或者提升发布到正式版`1.2.9.RELEASE`，具体可以参考：

```shell
# 确认切换到当前分支
git checkout 1.x

# 更新下代码，检查下 `git log` 确保当前代码正确
git pull

# 切换到主版本分支
git checkout master

# 合并开发分支到主版本分支
git merge 1.x

# 修改pom版本
编辑项目根目录下的 pom.xml 文件中的 `<properties>` 下的 `<revision>x.x.x.RELEASE</revision>` 标签中版本信息
修改为当前最新 SNAPSHOT版本编号，并将后缀变为 `.RELEASE`（如当前合并过来的1.x分支的最新SNAPSHOT版本为 `1.2.9-SNAPSHOT` 则修改为 `1.2.9.RELEASE`）

# 在发版日历中添加发版描述
修改发版日历 [version](VERSION.md) 
1. 修改《当前最新版本》
2. 修改《更新日历》增加当前版本的版本号、发布时间、贡献人、变更内容 （倒序，在最前边添加即可）
3. 修改《目录》增加当前版本的版本号

# 提交最后一次的改动，这里的 commit 内容为：当前的RELEASE版本号 + 变更内容
git add pom.xml VERSION.md && git commit -m '1.2.9.RELEASE'

# master分支打tag
git tag 1.2.9.RELEASE

# 推送tag到git服务器
git push $ORIGIN 1.2.9.RELEASE

# 发版
mvn clean deploy -D revision=1.2.9.RELEASE -P deploy
```

## 详述

项目使用`flatten`插件的`revision`模式统一`module`版本和项目版本并整理`pom`文件。

项目共有多个分支，其中`master`分支为主分支，`1.x、2.x`等是以主版本命名的分支。

项目使用了多`profiles`进行管理，隔离远程`sg maven`仓库，保护版本内容.当发版时，要指定`deploy`这个`profile`，可以在`idea maven`组件`Profiles`列表勾选，也可在编译参数上添加`-P deploy`，例如`mvn clean deploy -P deploy`
，如果有多个`profile`需要指定可以通过`mvn clean deploy -P deploy -P $otherProfiles`。将项目安装到本地`（mvn clean install）`或普通编译`（mvn clean compile）`时建议不要指定`deploy`，即使没有执行`deploy goal`
。完整的编译命令请参考文件末的[示例](#%E7%A4%BA%E4%BE%8B)。

1. 三版本号机制，主版本.次版本.修订号。有新的`feature`一般增加次版本，修订号归零；有里程碑的改变时增加主版本，次版本看情况，修订号归零；完善逻辑、改变某一实现方式、修复`bug`、改变`log`行为或注释（注释一定不会单独发`RELEASE`版）通常增加修订版本。无论哪个号码变化，最终`deploy`
   之前，建议都要经过`SNAPSHOT`；

2. 在确认发布`RELEASE`新版本时，首先合并最新的主版本分支到默认分支（通常是`master`或`main`）分支（保证该主版本最新`commit`是完整且现阶段无重大问题），然后在`master`分支标记 tag，然后发版；

3. 在发布`SNAPSHOT`新版本时，在对应主版本分支标记`tag`，然后再发版；

4. 一个项目理想情况是有一个分支处在`snapshot`状态，如果多个，要么是项目大（`Spring boot`的分支是具体到次版本的，比如`2.3.x`），要么处在`bug`验证期间。

## 特殊版本-修订历史版本

接上述示例，此时`1.x`分支已经是`1.3`之后的版本了，如果这时发现`1.2.0.RELEASE`（或更早的比如 `1.0.5.RELEASE`）有`bug`或某代码块实现有更好的方式或缺少了重要的`log`，建议的做法是，

1. 切到`master`分支，从`1.2.0.RELEASE`对应的`commit ID``checkout`出来一个新分支 `1.2.1-SNAPSHOT`；

2. 本地开发完后，在本地进行编译、测试、预发；

3. 合格后，合并到`master`上，打`1.2.1.RELEASE` `tag`、推远程；

NOTE: **要注意的时，如果是修改`bug`，一定要在文档维护`bug`的来源、场景以及影响范围。**