# Contributing Guidelines

 👋 Hi, we are glad to see your enthusiasm for the [PowerNukkitX](https://github.com/PowerNukkitX/PowerNukkitX) project, our goal is to provide a good collaborative environment for all participants involved in the development, so we The following guidelines are based on past experience and are a list of things you should keep in mind during the process.

 You should note, however, that these rules are not "**official rules**" per se, but that following them will help participants to be more efficient in their processes.

## CataLogs

1. <a href="#Catalogs-Faq#1">🧾 I would like to submit an issue!</a>
2. <a href="#Catalogs-Faq#2">💡 I would like to submit a pull request!</a>
3. <a href="#Catalogs-Swlang">🌐 Switch Languages / 切换语言</a>

## <a id="Catalogs-Faq#1"></a>🧾 I would like to submit an issue!

Issues, bug reports and feature suggestions are welcomed, though please keep in mind that at any point in time, hundreds of issues are open, which vary in severity and the amount of time needed to address them. As such it's not uncommon for issues to remain unresolved for a long time or even closed outright if they are deemed not important enough to fix in the foreseeable future.

* **Before submitting an issue, try searching existing issues first.**

  For housekeeping purposes, we close issues that overlap with or duplicate other pre-existing issues - you can help us not to have to do that by searching existing issues yourself first. The issue search box, as well as the issue tag system, are tools you can use to check if an issue has been reported before.

* **When submitting a bug report, please try to include as much detail as possible.**

  Bugs are not equal - some of them will be reproducible every time on pretty much all hardware, while others will be hard to track down due to being specific to particular hardware or even somewhat random in nature. As such, providing as much detail as possible when reporting a bug is hugely appreciated. A good starting set of information consists of:

  * the server logs (when avaliable), which can be obtained with:
    * `/debugpaste upload`
    * `[Server path]/logs/latest.log`
  * your system specifications (including the operating system and platform you are playing on),
  * a reproduction scenario (list of steps you have performed leading up to the occurrence of the bug),
  * a video or picture of the bug, if at all possible.

* **Provide more information when asked to do so.**

  Sometimes when a bug is more elusive or complicated, none of the information listed above will pinpoint a concrete cause of the problem. In this case we will most likely ask you for additional info. Providing that information is beneficial to both parties - we can track down the problem better, and hopefully fix it for you at some point once we know where it is!

* **When submitting a feature proposal, please describe it in the most understandable way you can.**

  Communicating your idea for a feature can often be hard, and we would like to avoid any misunderstandings. As such, please try to explain your idea in a short, but understandable manner.

* **Refrain from posting "+1" comments.**

  If an issue has already been created, saying that you also experience it without providing any additional details doesn't really help us in any way. To express support for a proposal or indicate that you are also affected by a particular bug, you can use comment reactions instead.

## <a id="Catalogs-Faq#2"></a>💡  I would like to submit a pull request! 
<!--修改链接-->
We also welcome pull requests from unaffiliated contributors. The [issue tracker](https://github.com/powernukkitx/powernukkitx/issues) should provide plenty of issues that you can work on; we also mark issues that we think would be good for newcomers with the [`good first issue`](https://github.com/powernukkitx/powernukkitx/issues?q=is%3Aissue+is%3Aopen+label%3Agood%20first%20issue) label.

Here are some key things to note before jumping in:

* **Make sure you are comfortable with Java and your development environment.**<!--无需修改-->

  While we are accepting of all kinds of contributions, we also have a certain quality standard we'd like to uphold and limited time to review your code. Therefore, we would like to avoid providing entry-level advice, and as such if you're not very familiar with Java as a programming language, we'd recommend that you start off with a few personal projects to get acquainted with the language's syntax, toolchain and principles of object-oriented programming first.

* **Make sure you are familiar with git and the pull request workflow.**<!--修改最后一句-->

  [git](https://git-scm.com/) is a distributed version control system that might not be very intuitive at the beginning if you're not familiar with version control. In particular, projects using git have a particular workflow for submitting code changes, which is called the pull request workflow.

  To make things run more smoothly, we recommend that you look up some online resources to familiarise yourself with the git vocabulary and commands, and practice working with forks and submitting pull requests at your own pace. A high-level overview of the process can be found in [this article by GitHub](https://help.github.com/en/github/collaborating-with-issues-and-pull-requests/proposing-changes-to-your-work-with-pull-requests).

  We also provide a [handy link](https://github.com/powernukkitx/powernukkitx/pulls) for making pull requests to PowerNukkitX.

* **Make sure to submit pull requests off of a topic branch.**<!--无需修改-->

  As described in the article linked in the previous point, topic branches help you parallelise your work and separate it from the main `bleeding` branch, and additionally are easier for maintainers to work with. Working with multiple `bleeding` branches across many remotes is difficult to keep track of, and it's easy to make a mistake and push to the wrong `bleeding` branch by accident.

* **Add tests for your code whenever possible.**<!--无需修改-->

  Automated tests are an essential part of a quality and reliable codebase. They help to make the code more maintainable by ensuring it is safe to reorganise (or refactor) the code in various ways, and also prevent regressions - bugs that resurface after having been fixed at some point in the past. If it is viable, please put in the time to add tests, so that the changes you make can last for a (hopefully) very long time.

* **Run tests before opening a pull request.**<!--无需修改-->

  Tying into the previous point, sometimes changes in one part of the codebase can result in unpredictable changes in behaviour in other pieces of the code. This is why it is best to always try to run tests before opening a PR.

  Continuous integration will always run the tests for you (and us), too, but it is best not to rely on it, as there might be many builds queued at any time. Running tests on your own will help you be more certain that at the point of clicking the "Create pull request" button, your changes are as ready as can be.

* **Make sure that the pull request is complete before opening it.**<!--无需修改-->

  Whether it's fixing a bug or implementing new functionality, it's best that you make sure that the change you want to submit as a pull request is as complete as it can be before clicking the *Create pull request* button. Having to track if a pull request is ready for review or not places additional burden on reviewers.

  Draft pull requests are an option, but use them sparingly and within reason. They are best suited to discuss code changes that cannot be easily described in natural language or have a potential large impact on the future direction of the project. When in doubt, don't open drafts unless a maintainer asks you to do so.

* **Only push code when it's ready.**<!--无需修改-->

  As an extension of the above, when making changes to an already-open PR, please try to only push changes you are reasonably certain of. Pushing after every commit causes the continuous integration build queue to grow in size, slowing down work and taking up time that could be spent verifying other changes.

* **Make sure to keep the *Allow edits from maintainers* check box checked.**<!--无需修改-->

  To speed up the merging process, collaborators and team members will sometimes want to push changes to your branch themselves, to make minor code style adjustments or to otherwise refactor the code without having to describe how they'd like the code to look like in painstaking detail. Having the *Allow edits from maintainers* check box checked lets them do that; without it they are forced to report issues back to you and wait for you to address them.

* **Refrain from continually merging the master branch back to the PR.**<!--无需修改-->

  Unless there are merge conflicts that need resolution, there is no need to keep merging `bleeding` back to a branch over and over again. One of the maintainers will merge `bleeding` themselves before merging the PR itself anyway, and continual merge commits can cause CI to get overwhelmed due to queueing up too many builds.

* **Refrain from force-pushing to the PR branch.**<!--无需修改-->

  Force-pushing should be avoided, as it can lead to accidentally overwriting a maintainer's changes or CI building wrong commits. We value all history in the project, so there is no need to squash or amend commits in most cases.

  The cases in which force-pushing is warranted are very rare (such as accidentally leaking sensitive info in one of the files committed, adding unrelated files, or mis-merging a dependent PR).

* **Be patient when waiting for the code to be reviewed and merged.**<!--完成-->

  While we want to review all contributions as quickly as possible, our time is limited because team members must handle their own tasks in addition to reviewing code. Therefore, work needs to be prioritized and unfortunately, your PR may take days or weeks to be merged, depending on how important it is considered to be.

* **Don't mistake criticism of code for criticism of your person.**<!--无需修改-->

  As mentioned before, we are highly committed to quality when it comes to the project. This means that contributions from less experienced community members can take multiple rounds of review to get to a mergeable state. We try our utmost best to never conflate a person with the code they authored, and to keep the discussion focused on the code at all times. Please consider our comments and requests a learning experience, and don't treat it as a personal attack.

* **Feel free to reach out for help.**<!--完成-->

  If you're uncertain about some part of the codebase or some inner workings of the game and framework, please reach out either by leaving a comment in the relevant issue or PR thread, or by posting a message in the [Discord server](https://discord.gg/j7UwsaNu4V). We will try to help you as much as we can.

  When it comes to which form of communication is best, GitHub generally lends better to longer-form discussions, while Discord is better for snappy call-and-response answers. Use your best discretion when deciding, and try to keep a single discussion in one place instead of moving back and forth.

## <a id="Catalogs-Swlang"></a>🌐 Multilingual documentation

---
Need to switch languages? 

[![简体中文](https://img.shields.io/badge/简体中文-100%25-green?style=flat-square)](https://github.com/PowerNukkitX/PowerNukkitX/blob/master/CONTRIBUTING.md)
[![繁體中文](https://img.shields.io/badge/繁體中文-100%25-green?style=flat-square)](https://github.com/PowerNukkitX/PowerNukkitX/blob/master/blob/zh-cht/CONTRIBUTING.md)
[![English](https://img.shields.io/badge/English-100%25-green?style=flat-square)](https://github.com/PowerNukkitX/PowerNukkitX/blob/master/blob/en-us/CONTRIBUTING.md)
[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg?style=flat-square)](https://github.com/PowerNukkitX/PowerNukkitX/blob/master/LICENSE)
[![ChangeLog](https://img.shields.io/badge/ChangeLog-blue?style=flat-square)](https://github.com/PowerNukkitX/PowerNukkitX/blob/master/blob/en-us/CHANGELOG.md)
[![PNX-DOC](https://img.shields.io/badge/PNX-Doc-blue?style=flat-square)](https://doc.powernukkitx.cn)
