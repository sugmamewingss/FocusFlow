package com.focusflow.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.focusflow.app.data.local.dao.*
import com.focusflow.app.data.local.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        User::class,
        FocusSession::class,
        VirtualAsset::class,
        UserInventory::class,
        AppWhitelist::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun focusSessionDao(): FocusSessionDao
    abstract fun virtualAssetDao(): VirtualAssetDao
    abstract fun userInventoryDao(): UserInventoryDao
    abstract fun appWhitelistDao(): AppWhitelistDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "focusflow_database"
                )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Populate initial data
                            INSTANCE?.let { database ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    populateDatabase(database)
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun populateDatabase(database: AppDatabase) {
            val assetDao = database.virtualAssetDao()

            // Populate initial virtual assets
            val initialAssets = listOf(
                // === POHON (Trees) ===
                VirtualAsset(
                    assetName = "Pohon Oak",
                    price = 50,
                    assetType = "Flora",
                    iconResource = "tree_oak",
                    description = "Pohon kokoh yang tumbuh dari fokus Anda"
                ),
                VirtualAsset(
                    assetName = "Pohon Pinus",
                    price = 75,
                    assetType = "Flora",
                    iconResource = "tree_pine",
                    description = "Pohon hijau yang menenangkan"
                ),
                VirtualAsset(
                    assetName = "Pohon Apel",
                    price = 80,
                    assetType = "Flora",
                    iconResource = "tree_apple",
                    description = "Pohon dengan daun merah yang indah"
                ),
                VirtualAsset(
                    assetName = "Pohon Sawit",
                    price = 999,
                    assetType = "Flora",
                    iconResource = "tree_willow",
                    description = "Kesukaan wowok"
                ),
                VirtualAsset(
                    assetName = "Pohon Bambu",
                    price = 65,
                    assetType = "Flora",
                    iconResource = "bamboo",
                    description = "Simbol fleksibilitas dan kekuatan"
                ),
                VirtualAsset(
                    assetName = "Bonsai",
                    price = 120,
                    assetType = "Flora",
                    iconResource = "bonsai",
                    description = "Seni miniatur pohon Jepang"
                ),

                // === BUNGA (Flowers) ===
                VirtualAsset(
                    assetName = "Bunga Sakura",
                    price = 100,
                    assetType = "Flora",
                    iconResource = "flower_sakura",
                    description = "Keindahan yang mekar dari dedikasi"
                ),
                VirtualAsset(
                    assetName = "Bunga Lotus",
                    price = 110,
                    assetType = "Flora",
                    iconResource = "flower_lotus",
                    description = "Simbol pencerahan dan kemurnian"
                ),
                VirtualAsset(
                    assetName = "Bunga Lavender",
                    price = 85,
                    assetType = "Flora",
                    iconResource = "flower_lavender",
                    description = "Aroma menenangkan untuk pikiran"
                ),
                VirtualAsset(
                    assetName = "Taman Bunga Kecil",
                    price = 95,
                    assetType = "Flora",
                    iconResource = "flower_garden",
                    description = "Kumpulan bunga berwarna-warni"
                ),

                // === BANGUNAN & HIASAN (Buildings & Decorations) ===
                VirtualAsset(
                    assetName = "Kolam Kecil",
                    price = 150,
                    assetType = "Building",
                    iconResource = "pond",
                    description = "Air tenang yang merefleksikan ketenangan pikiran"
                ),
                VirtualAsset(
                    assetName = "Batu Zen",
                    price = 80,
                    assetType = "Building",
                    iconResource = "zen_rock",
                    description = "Batu yang menyeimbangkan energi pulau"
                ),
                VirtualAsset(
                    assetName = "Jembatan Kayu",
                    price = 180,
                    assetType = "Building",
                    iconResource = "bridge",
                    description = "Jembatan menuju kedamaian"
                ),
                VirtualAsset(
                    assetName = "Lentera Jepang",
                    price = 100,
                    assetType = "Building",
                    iconResource = "lantern",
                    description = "Cahaya yang menerangi jalan"
                ),
                VirtualAsset(
                    assetName = "Pagoda Mini",
                    price = 200,
                    assetType = "Building",
                    iconResource = "pagoda",
                    description = "Menara ketenangan spiritual"
                ),
                VirtualAsset(
                    assetName = "Taman Zen",
                    price = 160,
                    assetType = "Building",
                    iconResource = "zen_garden",
                    description = "Pasir dan batu untuk meditasi"
                ),
                VirtualAsset(
                    assetName = "Air Terjun Kecil",
                    price = 220,
                    assetType = "Building",
                    iconResource = "waterfall",
                    description = "Suara air yang menenangkan"
                ),
                VirtualAsset(
                    assetName = "Gazebo Kayu",
                    price = 250,
                    assetType = "Building",
                    iconResource = "gazebo",
                    description = "Tempat berteduh dan merenung"
                ),
                VirtualAsset(
                    assetName = "Patung Moai",
                    price = 190,
                    assetType = "Building",
                    iconResource = "buddha_statue",
                    description = "Simbol turun menurun"
                ),

                // === HEWAN (Animals) ===
                VirtualAsset(
                    assetName = "Blowfish",
                    price = 80,
                    assetType = "Animal",
                    iconResource = "Blowfish",
                    description = "Ikan yang menggembung"
                ),
                VirtualAsset(
                    assetName = "Ikan Tropik",
                    price = 150,
                    assetType = "Animal",
                    iconResource = "tropik_fish",
                    description = "Simbol kebebasan eksplor"
                ),
                VirtualAsset(
                    assetName = "Flamingo",
                    price = 250,
                    assetType = "Animal",
                    iconResource = "flamingo",
                    description = "Simbol kecantikan"
                ),
                VirtualAsset(
                    assetName = "Kupu-kupu",
                    price = 70,
                    assetType = "Animal",
                    iconResource = "butterfly",
                    description = "Transformasi yang indah"
                ),
                VirtualAsset(
                    assetName = "Burung Kolibri",
                    price = 90,
                    assetType = "Animal",
                    iconResource = "hummingbird",
                    description = "Energi dan kegembiraan"
                ),
                VirtualAsset(
                    assetName = "Rusa Kecil",
                    price = 170,
                    assetType = "Animal",
                    iconResource = "deer",
                    description = "Keanggunan dan kelembutan"
                ),
                VirtualAsset(
                    assetName = "Kelinci",
                    price = 110,
                    assetType = "Animal",
                    iconResource = "rabbit",
                    description = "Kelincahan dan keceriaan"
                ),
                VirtualAsset(
                    assetName = "Burung Hantu",
                    price = 150,
                    assetType = "Animal",
                    iconResource = "owl",
                    description = "Kebijaksanaan dalam keheningan"
                ),
                VirtualAsset(
                    assetName = "Rubah",
                    price = 160,
                    assetType = "Animal",
                    iconResource = "fox",
                    description = "Kecerdasan dan kelincahan"
                ),

                // === CUACA (Weather) ===
                VirtualAsset(
                    assetName = "Hujan Rintik",
                    price = 200,
                    assetType = "Weather",
                    iconResource = "rain",
                    description = "Suara hujan yang menenangkan pikiran"
                ),
                VirtualAsset(
                    assetName = "Kabut Pagi",
                    price = 180,
                    assetType = "Weather",
                    iconResource = "fog",
                    description = "Keheningan misterius pagi hari"
                ),
                VirtualAsset(
                    assetName = "Pelangi",
                    price = 250,
                    assetType = "Weather",
                    iconResource = "rainbow",
                    description = "Harapan setelah badai"
                ),
                VirtualAsset(
                    assetName = "Salju Halus",
                    price = 220,
                    assetType = "Weather",
                    iconResource = "snow",
                    description = "Ketenangan musim dingin"
                ),
                VirtualAsset(
                    assetName = "Kunang-kunang",
                    price = 190,
                    assetType = "Weather",
                    iconResource = "fireflies",
                    description = "Cahaya kecil di malam hari"
                )
            )

            initialAssets.forEach { asset ->
                assetDao.insertAsset(asset)
            }
        }
    }
}