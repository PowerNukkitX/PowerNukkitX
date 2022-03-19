# 贡献帮助指南
 👋 Hi，我们很高兴见到您对[PowerNukkitX](https://github.com/PowerNukkitX/PowerNukkitX)项目所表现出的热情，我们 的目标是为所有参与开发的参与者们提供一个良好的协作环境，因此我们决定列出一些在此过程中需要您牢记的事情，以下指南是根据过往经验总结而成的。

 但您需注意，这些规则本身不是"**官方规则**"，但您若遵循它们将帮助参与者们提高处理效率。

## 目录

1. <a href="#目录-问题#1">🧾 我想提交一个问题!</a>
2. <a href="#目录-问题#2">💡 我想提交拉取申请!</a>
3. <a href="#目录-多语言文档">🌐 切换语言 / Switch Languages</a>

## <a id="目录-问题#1"></a>🧾 我想提交一个问题!

 我们随时欢迎您提出任何问题，错误报告和功能建议，但请记住，如果该问题在可预期的时间点内被认定为不重要，无法解决等，我们也会将其忽略，因此Issues页面可能会出现长时间未解决或彻底关闭的问题反馈将会是正常情况。

* **在提交问题报告前，请您先搜索现有问题列表。**

  出于管理目的，我们会关闭与先前就存在的问题反馈，避免产生重复，推荐您先通过自行搜索的方式查询是否存在类似问题。

* **在提交问题报告时请尽可能的尝试提供更多详细信息。**

 由于产生的错误信息各不相同，其中一部分错误在几乎所有设备上都能遇到，而有一部分是因为某些特定情况甚至是硬件本身导致，所以我们推荐您在提交问题时提供尽可能多且详细的信息，以下为示例模板：

  * 服务器的日志文件（若服务器可用时），您则可用通过以下方式获取日志文件：
    * `/debugpaste upload`
    * `[您的服务端所在文件夹]/logs/latest.log`
  * 你的设备规格参数 (包括您客户端的类型，操作系统平台，服务器硬件参数等),
  * 重现步骤 (您在遇到BUG之前进行了什么操作等),
  * 如果可以的话，请提供视频或截图加以辅助。

* **当被要求提供更多有关信息时。**

  在某些情况下，某个错误可能会难以复现等，当上面反馈的信息都不能很好的帮助我们追踪问题时，在这种情况下，我们可能会要求您提供额外的信息以便更好的帮助追踪问题，并且我们一旦知道问题所在时，就会着手开始修复并与您取得联系。

* **提交改进意见时，请以简单易懂的方式描述它。**

  有时传达您对某个功能的想法通常很困难，我们希望避免产生任何误解，因此，您在提交改进建议时，尽量使用简洁易懂的话语或方式描述您的想法。

* **避免发布"+1"重复问题。**

  如果问题已经产生，并且你也遇到过但无法提供任何对我们有帮助的问题细节的话，那您可在相关问题下评论留言。


## <a id="目录-问题#2"></a>我想提交拉取申请!

同时我们也欢迎各位开发者们对该项目作出您的贡献，您可在[问题追踪器](https://github.com/PowerNukkitX/PowerNukkitX/issues)页面发表您可以解决的问题等，同时我们也会使用[`Good First issue`](https://github.com/PowerNukkitX/PowerNukkitX/issues?q=is%3Aissue+is%3Aopen+label%3Agood%20first%20issue)标签标记出我们认为对新人有帮助的问题解答等。

⚠ 若您想参与该项目，则您需要注意一些事项：

* **确保您对Java编程有一定的基础的同时，还需熟悉对IDE工具的使用等。**

  虽然我们接受各种类型的贡献，但我们也有一定的代码质量要求，但我们审查您提交的代码时间是有限的，因此，我们希望避免提供入门建议等，如果您对Java编程不是很熟悉，我们建议您从一些个人项目开始，并熟悉语法，工具链等。

* **您需熟悉Git和Pull Request工作流程**

  [git](https://git-scm.com/) is a distributed version control system that might not be very intuitive at the beginning if you're not familiar with version control. In particular, projects using git have a particular workflow for submitting code changes, which is called the pull request workflow.

  To make things run more smoothly, we recommend that you look up some online resources to familiarise yourself with the git vocabulary and commands, and practice working with forks and submitting pull requests at your own pace. A high-level overview of the process can be found in [this article by GitHub](https://help.github.com/en/github/collaborating-with-issues-and-pull-requests/proposing-changes-to-your-work-with-pull-requests).

  We also provide a [handy link](https://powernukkit.org/pr) for making pull requests to PowerNukkitX: `https://github.com/powernukkitx/powernukkitx/pulls`.

* **Make sure to submit pull requests off of a topic branch.**

  As described in the article linked in the previous point, topic branches help you parallelise your work and separate it from the main `bleeding` branch, and additionally are easier for maintainers to work with. Working with multiple `bleeding` branches across many remotes is difficult to keep track of, and it's easy to make a mistake and push to the wrong `bleeding` branch by accident.

* **Add tests for your code whenever possible.**

  Automated tests are an essential part of a quality and reliable codebase. They help to make the code more maintainable by ensuring it is safe to reorganise (or refactor) the code in various ways, and also prevent regressions - bugs that resurface after having been fixed at some point in the past. If it is viable, please put in the time to add tests, so that the changes you make can last for a (hopefully) very long time.

* **Run tests before opening a pull request.**

  Tying into the previous point, sometimes changes in one part of the codebase can result in unpredictable changes in behaviour in other pieces of the code. This is why it is best to always try to run tests before opening a PR.

  Continuous integration will always run the tests for you (and us), too, but it is best not to rely on it, as there might be many builds queued at any time. Running tests on your own will help you be more certain that at the point of clicking the "Create pull request" button, your changes are as ready as can be.

* **Make sure that the pull request is complete before opening it.**

  Whether it's fixing a bug or implementing new functionality, it's best that you make sure that the change you want to submit as a pull request is as complete as it can be before clicking the *Create pull request* button. Having to track if a pull request is ready for review or not places additional burden on reviewers.

  Draft pull requests are an option, but use them sparingly and within reason. They are best suited to discuss code changes that cannot be easily described in natural language or have a potential large impact on the future direction of the project. When in doubt, don't open drafts unless a maintainer asks you to do so.

* **只有当代码准备好了才推送。**

  作为上述内容的延伸，当对一个已经开放的PR进行修改时，请尽量只推送你有合理把握的修改。每次提交后的推送都会导致持续自动构建队列的规模扩大，减缓工作速度，并占用可以用来验证其他修改的时间。

* **Make sure to keep the *Allow edits from maintainers* check box checked.**

  To speed up the merging process, collaborators and team members will sometimes want to push changes to your branch themselves, to make minor code style adjustments or to otherwise refactor the code without having to describe how they'd like the code to look like in painstaking detail. Having the *Allow edits from maintainers* check box checked lets them do that; without it they are forced to report issues back to you and wait for you to address them.

* **Refrain from continually merging the master branch back to the PR.**

  Unless there are merge conflicts that need resolution, there is no need to keep merging `bleeding` back to a branch over and over again. One of the maintainers will merge `bleeding` themselves before merging the PR itself anyway, and continual merge commits can cause CI to get overwhelmed due to queueing up too many builds.

* **Refrain from force-pushing to the PR branch.**

  Force-pushing should be avoided, as it can lead to accidentally overwriting a maintainer's changes or CI building wrong commits. We value all history in the project, so there is no need to squash or amend commits in most cases.

  The cases in which force-pushing is warranted are very rare (such as accidentally leaking sensitive info in one of the files committed, adding unrelated files, or mis-merging a dependent PR).

* **在等待代码被审查和合并的过程中要有耐心。**

  尽管我们希望尽可能快地审查所有的贡献，但我们的时间是有限的，因为团队成员除了审查代码外，还必须处理自己的任务。因此，工作需要被优先考虑，不幸的是，你的PR可能需要数天或数周才能被合并，这取决于它被认为是多么重要。

* **不要把对代码的批评误认为是对你个人的批评。**

  如前所述，当涉及到项目时，我们对质量有高度的承诺。这意味着，来自经验不足的社区成员的贡献可能需要多轮审查才能达到可合并的状态。我们尽最大努力不把一个人和他所写的代码混为一谈，并且在任何时候都把讨论集中在代码上。请把我们的评论和要求看作是一种学习经验，不要把它当作是一种人身攻击。

* **我们随时欢迎您联系我们或寻求帮助。**

  如果你对代码库的某些部分或游戏和框架的某些内部运作不确定，请通过在相关问题或PR中留言，或在[Discord](https://discord.gg/j7UwsaNu4V)&[QQ群](https://jq.qq.com/?_wv=1027&k=6rm3gbUI)发文来联系我们。我们会尽可能地帮助你。

  说到哪种沟通方式最好，GitHub通常更适合长篇大论，而Discord和QQ群则更适合快速的呼唤和回应。在决定的时候，请尽力斟酌，并尽量将一个讨论放在一个地方，而不是来回移动。

## <a id="目录-多语言文档"></a>🌐 多语言文档

---
Need to switch languages? 

[![简体中文](https://img.shields.io/badge/简体中文-100%25-green?style=flat-square)](https://github.com/PowerNukkitX/PowerNukkitX/blob/master/CONTRIBUTING.md)
[![English](https://img.shields.io/badge/English-todo-red?style=flat-square)](https://github.com/PowerNukkitX/PowerNukkitX/blob/master/blob/en-us/CONTRIBUTING.md)
[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg?style=flat-square)](https://github.com/PowerNukkitX/PowerNukkitX/blob/master/LICENSE)
[![ChangeLog](https://img.shields.io/badge/更新日志-blue?style=flat-square)](https://github.com/PowerNukkitX/PowerNukkitX/blob/master/CHANGELOG.md)