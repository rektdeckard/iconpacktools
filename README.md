<img src="https://i.imgur.com/7oCSH5V.png" width="192" align="right" hspace="20" />

# Icon Pack Tools
A handy utility for Android icon pack creators to generate key assets and resources, and alleviate much of the manual work. Made a bunch of new icons and need to update your `drawable.xml` and `icon-pack.xml` files? Just drag and drop. Keeping track of your app filters in three places at once? No longer. Just create your `appfilter.xml`, and we'll generate `appmap.xml` and `theme_resources.xml` for you. Even merge app filter files from your icon requests into your master filter files.

[![GitHub stars](https://img.shields.io/github/stars/rektdeckard/iconpacktools?style=flat-square&label=Star)](https://github.com/rektdeckard/iconpacktools)
[![GitHub forks](https://img.shields.io/github/forks/rektdeckard/iconpacktools?style=flat-square&label=Fork)](https://github.com/rektdeckard/iconpacktools/fork)
[![GitHub watchers](https://img.shields.io/github/watchers/rektdeckard/iconpacktools?style=flat-square&label=Watch)](https://github.com/rektdeckard/iconpacktools)
[![Follow on GitHub](https://img.shields.io/github/followers/rektdeckard?style=flat-square&label=Follow)](https://github.com/rektdeckard)

[![Twitter Follow](https://img.shields.io/twitter/follow/friedtm.svg?style=flat-square)](https://twitter.com/friedtm)

## Installation

### Build from source
Clone the repository into the directory of your choice. Compile and package using Gradle:
```bash
$ cd path/to/iconpacktools
$ gradle clean build shadowJar
$ java -jar build/libs/iconpacktools-<VERSION>-all.jar
```
### Binaries
Download one of the linked binaries:

- Windows [coming soon]
- macOS [coming soon]
- Linux [coming soon]
  
### Jar
Download the most recent packaged jar from the [releases](https://github.com/rektdeckard/iconpacktools/releases) page. Requires a Java Runtime to be installed on your machine, any jre version above 1.7 should have no problem running it:
```bash
$ java -jar path/to/iconpacktools-<VERSION>-all.jar
```
