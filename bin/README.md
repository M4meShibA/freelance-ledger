# 帳簿 — 個人事業主向け 収入・経費管理アプリ

## デザインコンセプト

「和紙の帳簿」をイメージしたUI。生成り色の紙の地に、墨色のインクと、
収入(緑)・経費(朱色)の2色だけで情報を伝える構成にしている。
SaaS的なカードの羅列ではなく、罫線で区切ったテーブルを中心に、
数字は等幅フォントで右揃えにして、実際の帳簿に近い読みやすさを意識した。

見出しには明朝体(Shippori Mincho)、本文にはゴシック体を使い、
2つのフォントだけで統一感を出している。

個人事業主(フリーランス)が、収入・経費を記録して収支を把握するためのWebアプリ。
Java / Spring Boot のポートフォリオ用プロジェクト。

## v1でできること

- ユーザー登録・ログイン(Spring Security)
- 収入の登録・一覧・編集・削除
- 経費の登録・一覧・編集・削除(勘定科目つき、初回起動時にデフォルトの科目を自動投入)
- 月別・年別のダッシュボード(CSSのみのシンプルな棒グラフ)

## v2でできること

- 所得税・住民税・国民健康保険料の概算計算(青色申告特別控除65万円+基礎控除を適用した簡易試算)
- 年間レポートのPDF出力(月別内訳+税金概算。日本語CIDフォントを埋め込みなしで使用)

**注意**: 税金の計算とPDFレポートは、学習・ポートフォリオ用の簡易試算です。
実際の確定申告には使わず、正確な金額は税理士または確定申告ソフトで確認してください。

## 技術構成

- Java 25(LTS)
- Spring Boot 3.5.5(Web, Thymeleaf, Data JPA, Security, Validation)
- Supabase(PostgreSQL)/ H2(オフライン動作確認用)
- Maven
- OpenPDF(年間レポートのPDF出力)

## ローカルでの動かし方(Supabase接続)

このプロジェクトはSupabase(PostgreSQL)に接続する設定になっている。

1. [supabase.com](https://supabase.com)でプロジェクトを作成する
2. ダッシュボードの `Connect` > `Session pooler` > `JDBC` タブで接続文字列をコピーする
   (`jdbc:postgresql://xxxx.pooler.supabase.com:5432/postgres?user=postgres.xxxx&password=xxxx&sslmode=require` の形)
3. 環境変数 `SUPABASE_DB_URL` にその接続文字列を設定する

   PowerShellの場合:
   ```powershell
   $env:SUPABASE_DB_URL = "jdbc:postgresql://xxxx.pooler.supabase.com:5432/postgres?user=postgres.xxxx&password=xxxx&sslmode=require"
   ```

   この設定は、PowerShellを閉じると消える。毎回入力するのが面倒な場合は、
   「システム環境変数」に登録すれば、PowerShellを開き直すだけで使えるようになる。

4. アプリを起動する
   ```bash
   mvn spring-boot:run
   ```

起動後、ブラウザで `http://localhost:8080/register` を開いてユーザー登録し、
`http://localhost:8080/login` からログインすると `/dashboard` に遷移する。

パスワードを含む接続文字列は、**`application.properties`に直接書かない**こと。
`.gitignore`で`.env`などは除外しているが、環境変数経由にしているのは
コードやGit履歴にパスワードを残さないための基本的な配慮。面接でも
説明しやすいポイント。

**H2(インメモリDB)に戻したいとき**: `application.properties`の
Supabase設定をコメントアウトし、下にあるH2設定のコメントを外せば、
ネットなしでもすぐ動作確認ができる。

## ディレクトリ構成

```
src/main/java/com/example/freelanceledger/
├── FreelanceLedgerApplication.java
├── config/          … Spring Security, ログイン画面
├── user/            … ユーザー登録・認証
├── income/          … 収入のCRUD(実装済み)
├── expense/         … 経費のCRUD(実装済み)+ 勘定科目の初期データ投入
└── summary/         … 月別・年別サマリー、税金概算、PDF出力(実装済み)
```

## 次にやること(v3案)

1. DBの集計クエリ化(現在はメモリ内で全件フィルタしている点の改善)
2. PostgreSQL接続への切り替えと、本番想定の設定整理
3. PDFの日本語フォントを埋め込み型(IPAフォント等)に差し替える
4. 経費のカテゴリ別集計・グラフ追加
5. README・アプリ説明メモをポートフォリオ用に整える
