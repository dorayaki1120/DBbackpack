package dorayakiumai.gui;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import dorayakiumai.gui.backpack.CBackpackInventory;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class DBConnection {
    public Connection connection;
    private String table;

    public static Map<Integer, ItemStack> getMap1 = new HashMap<>();//putに使う変数に使う用の変数(?
    public static Multimap<UUID, Map<Integer, ItemStack>> getMap2 = ArrayListMultimap.create();//putに使う用の変数

    public DBConnection() {
        try(Statement statement = this.connection.createStatement()) {
            ResultSet result = statement.executeQuery("SHOW TABLES LIKE '" + this.table + "'");
            if (!result.next()) {
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS `" + this.table + "` (uuid` VARCHAR(36) NOT NULL, `isBoolean` BOOLEAN NOT NULL DEFAULT TRUE, PRIMARY KEY (`uuid`))");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void openConnection() {
        try {
            String host = GUI.inst().getConfig().getString("Database.host");
            int port = GUI.inst().getConfig().getInt("Database.port");
            table = GUI.inst().getConfig().getString("Database.Table");
            String database = GUI.inst().getConfig().getString("Database.database");
            String username = GUI.inst().getConfig().getString("Database.username");
            String password = GUI.inst().getConfig().getString("Database.password");
            if (connection == null || connection.isClosed()) { //でーたべーすに接続ができないor閉まってる場合
                synchronized (this) { //同時に起こさせる...?
                    if (connection != null && connection.isClosed()) {
                        return;
                    }
                }
                Class.forName("com.mysql.jdbc.Driver");    //こねくしょんを更新してまたデータベースに接続する...?
                connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public Inventory getbackpackdata(Player player) {
        try {
            openConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SHOW TABLES LIKE '" + this.table + "'");
            if (!resultSet.next()) { //データベースから引っ張ってこれなかったとき
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS `" + this.table + "` (`uuid` VARCHAR(36) NOT NULL, `slot` BIGINT NOT NULL, `itemData` MEDIUMBLOB)");
            }
            PreparedStatement state = connection.prepareStatement("SELECT `slot`, `itemData` FROM `" + this.table + "` WHERE `uuid` = ?");
            try {
                state.setString(1, player.getUniqueId().toString());
                ResultSet result = state.executeQuery();

                Inventory inv = Bukkit.createInventory(new CBackpackInventory(), 54, Component.text(ChatColor.AQUA + "そうこ"));

                while (result.next()) {
                    byte[] itemByte = result.getBytes("itemData");
                    int slot = result.getInt("slot");
                    ItemStack itemData = ItemStack.deserializeBytes(itemByte);
                    if (itemData.getType() == Material.STRUCTURE_VOID) {
                        itemData.setType(Material.AIR);
                }
                    inv.setItem(slot, itemData);

                    getMap1.put(slot, itemData);//key→slot,値→itemdata スロットのあいてむでたを保存...?
                    getMap2.put(player.getUniqueId(), getMap1);//key→uuid,値→getmap1? uuid(player)にすろっとのあいてむでたを保存...?

            }
                return inv;
        } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void setbackpackdata(Inventory inv, Player player) {
        try {
            openConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SHOW TABLES LIKE '" + this.table + "'");
            if (!resultSet.next()) { //uuid→プレイヤー slot→スロット(? itemdata→あいてむでた
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS `" + this.table + "` (`uuid` VARCHAR(36) NOT NULL, `slot` BIGINT NOT NULL, `itemData` MEDIUMBLOB)");
            } //0から始まって26以下の場合くりかえす
            for (int i = 0; i <= 26; i++) {
                ItemStack item = inv.getItem(i); //i=slot?
                byte[] itemByte;
                //Mapに保存
                getMap1.put(i, item);
                getMap2.put(player.getUniqueId(), getMap1);
                // アイテムをデータ化
                if (item == null) {  //アイテムがないとき?
                    ItemStack items = new ItemStack(Material.STRUCTURE_VOID);
                    itemByte = items.serializeAsBytes();
                } else {
                    itemByte = item.serializeAsBytes();
                }
                PreparedStatement state = connection.prepareStatement("INSERT INTO `" + this.table + "` (`uuid`, `slot`, `itemData`) VALUES (?, ?, ?);");
                try {
                    state.setString(1, player.getUniqueId().toString());//UUID
                    state.setInt(2, i);//slot
                    state.setBytes(3, itemByte);//あいてむでーた
                    state.executeUpdate();//ぷれぱれどすたてめんとに保存したやつをでーたべーすに保存する
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);     //えらーをきゃっちしたらエラーを投げる(?
        }
    }
}
    //わからないやつ
//connection.createStatement();   //接続を実行する...?
//resultSet  //保存したり引っぱり出したりできる
//statement.executeQuery("SHOW TABLES LIKE '" + this.backPackTable + "'");      //引数で指定されたSQL(ここだとたぶんbackpacktable)をデータベースからとってくる
//byte[] itemByte; //byte型のやつにアイテムを保存する...?
//map?   //キーを指定していろんな形でほぞんできる
//connection.prepareStatement //ぷれぱれがわからない
//try  //えらーがでてもじっこうするらしい
//state.executeUpdate();    //えらい
//catch //きゃっち    //あほえらい
//throw new RuntimeException //えらーちぇっく

  //Statement
//StatementインタフェースはSQLの実行に関するベースにになります。

  //問い合わせを実行
  //問い合わせの結果の取得
  //といった操作をしたい時にを使用します。

  //ResultSet
//上記のようににResultSetは以下の機能ももっています。

//SQLの実行結果を格納する
//DB情報を取得する

  //executeQuery
// executeQuery()メソッドは、引数で指定されたSQLをデータベースで実行するメソッドです。
//問題なく処理が完了すると、SQLの実行結果を格納したResultSet型のオブジェクトを返します。

  //byte[] itemByte;
//びっとが8こでいちばんちいさい

  //map
//Mapとは
  //java.util.Mapインタフェースは、
  //キーに対してキーに紐づく値を保持することができるコレクションの1つです。すごい
  //コレクションとは、後からサイズを変更できる動的配列と理解していただければ大丈夫です。?

  //PrepareStatement
//準備済みステートメント(?

  //state.executeUpdate()
//メソッドによるデータベース・オブジェクトの作成と変更
// JDBC メソッドの 1 つである Statement.executeUpdate を使用して、表を更新したり、
// ストアード・プロシージャー(つよそう)を呼び出したりできます。

  //catch
//tryに想定しなかったエラーがおこりそうなやつを入れて停止しないような回避策をcatchに入れる
//tryと一緒に使うことが多いらしい

  //throw new RuntimeException
//えらーには2種類ある
//error・EsceptionでEsceptionの中にRuntimeExceptionがある
//error→えぐいやばい
//Esception→回復見込みのある例外(? まぁまぁやばいやつとどうでもいいやつを拾う
//RuntimeException→回復しなくていい例外(???  どうでもいい奴をひろう
//えらーをキャッチできるから問題が起きないかチェックするのに使えるぽい
//Javaの例外処理とは
//基本的には例外はtry 内で throwして、ちゃんとcatchしましょうね、と習うと思います。 (e?
//playerごと(UUIDで判別)にデータベースを作ってバックパックを閉じたりログアウトしたときにバックパックのインベントリホルダーをデータとしてデータベースに保存してる...?