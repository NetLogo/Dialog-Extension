enablePlugins(org.nlogo.build.NetLogoExtension)

version    := "1.2.0"
isSnapshot := true

scalaVersion           := "3.7.0"
Compile / scalaSource  := baseDirectory.value / "src"
scalacOptions          ++= Seq("-deprecation", "-unchecked", "-Xfatal-warnings", "-encoding", "us-ascii", "-release", "17")

netLogoExtName       := "dialog"
netLogoClassManager  := "org.nlogo.extension.dialog.DialogExtension"
netLogoVersion       := "7.0.0-RC1-e8801f2"
