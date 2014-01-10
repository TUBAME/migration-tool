What's TUBAME
==============

TUBAMEはJBossマイグレーションを予定しているプロジェクトに対し、以下の情報を提供します。  
* アプリケーションに含まれる利用できないJavaパッケージやクラス情報(依存性検索機能)
* その他設定ファイルに含まれる修正が必要な箇所と修正工数の情報(ナレッジベース検索機能)

How do I run TUBAME?
==============
TUBAMEはEclipseのプラグインなため、インストールはpluginsフォルダに配置するだけです。
依存性検索はそのままで動作します。ナレッジベース検索に必要なRule-Set(xmlファイル)は、Knowledge Managerを利用して自ら作成できます。

Rule-Setの詳細なノウハウ(Guideline)は同様にknowledge Managerを利用して作成できます。

まずは、Joss EAP4からEAP6への移植時のRule-Setと詳細なノウハウをサンプルとして[ダウンロード可能](https://github.com/TUBAME/migration-knowledge/releases)なので、是非利用してみてください。今後徐々にWeblogicからJBossへのノウハウも公開していく予定です。皆様からのノウハウ投稿もお待ちしております。

詳細は[indexページ](http://tubame.github.io/migration-tool/index_ja.html)をご覧ください。
