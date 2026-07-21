package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalActivity
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BlueTurquoise
import com.example.ui.theme.CitronYellow
import com.example.ui.theme.DeepCurrent
import com.example.ui.viewmodel.EisseryViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CatalogItem(
    val id: String,
    val name: String,
    val description: String,
    val cost: Int,
    val emoji: String,
    val category: String
)

data class PremiumFood(
    val name: String,
    val description: String,
    val emoji: String,
    val category: String,
    val ingredients: List<String>,
    val instructions: List<String>
)

val premiumFoodsPool = listOf(
    PremiumFood(
        name = "Kobe Beef Tataki Salad",
        description = "Irisan tipis daging sapi Kobe premium setengah matang, disajikan di atas salad sayuran organik, microgreens, bit merah, dan dressing yuzu-truffle vinaigrette yang segar.",
        emoji = "🥩",
        category = "Gourmet Beef",
        ingredients = listOf(
            "150g Daging Sapi Kobe Premium (Slices)",
            "2 mangkuk Sayuran Hijau Organik (Arugula, Bayam Bayi)",
            "1/4 buah Bit Merah (iris tipis)",
            "2 sdm Yuzu-Truffle Vinaigrette",
            "Minyak Zaitun secukupnya"
        ),
        instructions = listOf(
            "Panaskan wajan anti-lengket dengan sedikit minyak zaitun hingga sangat panas.",
            "Panggang daging sapi Kobe selama 30 detik di setiap sisi (quick sear) agar bagian dalam tetap merah/setengah matang.",
            "Angkat daging, diamkan 1 menit, lalu iris tipis-tipis secara miring.",
            "Tata sayuran organik hias dan irisan bit merah di atas piring saji.",
            "Susun irisan daging Kobe di atas sayuran, lalu siram dengan saus Yuzu-Truffle Vinaigrette segar sebelum disajikan."
        )
    ),
    PremiumFood(
        name = "Norwegian Salmon Crudo with Avocado Caviar",
        description = "Irisan sashimi salmon Norwegia segar dengan alpukat mentega lembut, kaviar hitam mewah, minyak zaitun cold-pressed, perasan jeruk lemon Meyer, dan garam laut Maldon.",
        emoji = "🐟",
        category = "Premium Seafood",
        ingredients = listOf(
            "120g Salmon Norwegia segar (Kualitas Sashimi)",
            "1/2 buah Alpukat Mentega (potong dadu kecil)",
            "1 sdt Kaviar Hitam Mewah",
            "1 sdm Minyak Zaitun Extra Virgin (Cold-Pressed)",
            "1 sdt Perasan Lemon Meyer segar",
            "Sejumput Garam Laut Maldon & Lada Hitam"
        ),
        instructions = listOf(
            "Iris tipis salmon Norwegia dingin menggunakan pisau sashimi yang tajam.",
            "Susun irisan salmon secara melingkar dan rapi di atas piring dingin.",
            "Campurkan alpukat potong dadu dengan sedikit perasan lemon agar tidak mencokelat.",
            "Letakkan sesendok alpukat di bagian tengah salmon, lalu beri topping kaviar hitam di atasnya.",
            "Siram seluruh permukaan crudo dengan minyak zaitun extra virgin, perasan lemon, lalu taburi garam Maldon dan lada hitam tumbuk kasar."
        )
    ),
    PremiumFood(
        name = "Truffle Glazed Organic Duck Breast",
        description = "Dada bebek organik panggang dengan kulit garing madu herba, disiram saus reduction truffle liar hitam, disajikan dengan asparagus muda panggang dan puree ubi ungu.",
        emoji = "🦆",
        category = "Luxury Poultry",
        ingredients = listOf(
            "1 buah Dada Bebek Organik (kulit utuh)",
            "2 sdm Madu Herba Alami",
            "1 sdm Saus Reduction Truffle Hitam",
            "5 batang Asparagus Muda",
            "100g Puree Ubi Ungu Lembut",
            "Garam dan Lada secukupnya"
        ),
        instructions = listOf(
            "Kerat kulit dada bebek berbentuk silang tanpa mengenai dagingnya. Taburi garam dan lada.",
            "Letakkan dada bebek di wajan dingin (tanpa minyak), nyalakan api sedang agar lemak bebek meleleh keluar perlahan.",
            "Masak kulitnya selama 6-8 menit hingga sangat garing, balik dan masak sisi daging selama 3-4 menit. Angkat dan istirahatkan.",
            "Tumis asparagus di wajan bekas lemak bebek hingga renyah matang.",
            "Sajikan dada bebek yang telah diiris bersama puree ubi ungu hangat, asparagus, dan siram dengan saus reduction truffle hangat di atasnya."
        )
    ),
    PremiumFood(
        name = "Atlantic Lobster Tail Quinoa Risotto",
        description = "Ekor lobster Atlantic segar panggang dengan mentega bawang putih herba, disajikan di atas risotto quinoa organik lembut beraroma saffron emas dan keju parmigiano-reggiano premium.",
        emoji = "🦞",
        category = "Royal Seafood",
        ingredients = listOf(
            "1 ekor Ekor Lobster Atlantic segar",
            "80g Quinoa Organik (cuci bersih)",
            "2 siung Bawang Putih (cincang halus)",
            "1 sdm Mentega Bawang Putih Herba",
            "Sejumput Saffron Emas asli",
            "30g Keju Parmigiano-Reggiano parut",
            "250ml Kaldu Sayuran Hangat"
        ),
        instructions = listOf(
            "Gunting cangkang atas ekor lobster, keluarkan sedikit dagingnya ke atas cangkang. Olesi mentega bawang putih herba dan panggang di oven suhu 190°C selama 12-15 menit hingga matang.",
            "Tumis bawang putih cincang dengan sedikit mentega di panci kecil. Masukkan quinoa dan aduk rata.",
            "Tambahkan kaldu hangat yang sudah direndam saffron sedikit demi sedikit sambil diaduk perlahan hingga quinoa empuk dan meresap (risotto style).",
            "Masukkan parutan keju Parmigiano-Reggiano, aduk hingga risotto quinoa menjadi creamy.",
            "Sajikan risotto quinoa hangat di atas piring, lalu letakkan ekor lobster panggang di atasnya."
        )
    ),
    PremiumFood(
        name = "Seared Ahi Tuna Tartare Bowl",
        description = "Ahi Tuna kelas sashimi potong dadu dengan saus ponzu wijen pedas, disajikan di atas nasi merah kelapa, edamame segar, mangga manis, rumput laut wakame, dan alpukat iris.",
        emoji = "🍣",
        category = "Premium Fish",
        ingredients = listOf(
            "120g Ahi Tuna segar (Kualitas Sashimi)",
            "1 sdm Saus Ponzu Wijen Pedas",
            "1/2 mangkuk Nasi Merah Kelapa hangat",
            "2 sdm Edamame Kupas (rebus)",
            "1/4 buah Mangga Manis (potong dadu)",
            "1 sdm Rumput Laut Wakame",
            "1/4 buah Alpukat (iris tipis)"
        ),
        instructions = listOf(
            "Potong dadu kecil Ahi Tuna segar, letakkan di mangkuk.",
            "Campur tuna dengan saus ponzu wijen pedas hingga merata, diamkan 5 menit di kulkas agar bumbu meresap.",
            "Siapkan mangkuk saji, masukkan nasi merah kelapa sebagai dasar.",
            "Susun edamame rebus, potongan mangga manis, wakame, dan alpukat iris mengelilingi nasi.",
            "Letakkan racikan Ahi Tuna tartar di bagian tengah mangkuk, hias dengan taburan wijen sangrai."
        )
    ),
    PremiumFood(
        name = "Artisanal Grilled Halibut with Saffron Sauce",
        description = "Ikan Halibut liar panggang bumbu bumbu segar, disiram saus emulsi saffron lemon yang lembut, disajikan bersama brokoli rabe tumis bawang putih dan tomat ceri pusaka.",
        emoji = "🐟",
        category = "Exotic Seafood",
        ingredients = listOf(
            "150g Fillet Ikan Halibut Liar",
            "1 sdm Saus Emulsi Saffron Lemon",
            "1 ikat Brokoli Rabe",
            "4 buah Tomat Ceri Pusaka (belah dua)",
            "1 siung Bawang Putih (memarkan)",
            "Garam, Lada, dan Minyak Zaitun"
        ),
        instructions = listOf(
            "Keringkan fillet Halibut dengan tisu dapur. Lumuri dengan sedikit minyak zaitun, garam, dan lada.",
            "Panggang Halibut di atas wajan panggangan panas selama 3-4 menit di setiap sisi hingga matang merata. Angkat.",
            "Tumis brokoli rabe dan tomat ceri dengan bawang putih dan minyak zaitun hingga layu namun tetap renyah.",
            "Hangatkan saus emulsi saffron lemon dengan api sangat kecil.",
            "Tata brokoli rabe dan tomat ceri di piring saji, letakkan ikan Halibut panggang di atasnya, lalu siram dengan saus saffron lemon yang wangi."
        )
    ),
    PremiumFood(
        name = "Slow-Braised Wagyu Short Rib Bowl",
        description = "Iga pendek Sapi Wagyu yang dimasak lambat selama 24 jam hingga sangat empuk, disajikan di atas nasi kembang kol mentega herba dengan acar sayuran segar pelindung pencernaan.",
        emoji = "🍖",
        category = "Luxury Beef",
        ingredients = listOf(
            "180g Iga Pendek Sapi Wagyu (Short Rib)",
            "1 mangkuk Kembang Kol (parut kasar untuk nasi)",
            "1 sdm Mentega Herba berkualitas tinggi",
            "2 sdm Acar Sayuran Segar (Wortel, Timun, Lobak)",
            "Herba Aromatik (Thyme, Rosemary, Bawang Bombay)",
            "200ml Kaldu Sapi pekat"
        ),
        instructions = listOf(
            "Bumbui iga pendek Wagyu dengan garam dan lada. Panggang di wajan panas hingga kecokelatan di seluruh sisinya.",
            "Pindahkan ke panci slow cooker, tambahkan herba aromatik, bawang bombay, dan kaldu sapi. Masak dengan suhu rendah selama 8-12 jam hingga daging sangat empuk.",
            "Kukus kembang kol parut selama 5 menit, lalu tumis sebentar dengan mentega herba, garam, dan lada (Nasi Kembang Kol).",
            "Sajikan nasi kembang kol hangat di mangkuk.",
            "Letakkan iga pendek Wagyu empuk di atas nasi kembang kol, tuangkan sedikit jus sisa memasak iga, dan sajikan dengan acar segar pendamping."
        )
    ),
    PremiumFood(
        name = "Gourmet Avocado & King Crab Sourdough Toast",
        description = "Roti sourdough gandum kuno panggang dengan olesan tebal alpukat tumbuk, tumpukan daging Kepiting Raja Alaska manis segar, irisan lobak semangka, dan minyak herba klorofil.",
        emoji = "🥑",
        category = "Gourmet Toast",
        ingredients = listOf(
            "1 lembar Roti Sourdough Gandum Kuno (tebal)",
            "1/2 buah Alpukat Mentega matang",
            "60g Daging Kepiting Raja Alaska (Alaska King Crab)",
            "3 irisan tipis Lobak Semangka",
            "1 sdt Minyak Herba Klorofil",
            "Perasan Jeruk Nipis, Garam, dan Lada"
        ),
        instructions = listOf(
            "Panggang roti sourdough di atas wajan dengan sedikit minyak zaitun hingga cokelat keemasan dan renyah.",
            "Hancurkan alpukat mentega kasar dengan garpu, campur dengan sedikit perasan jeruk nipis, garam, dan lada.",
            "Oleskan alpukat tumbuk secara tebal di atas roti sourdough panggang hangat.",
            "Tata daging Kepiting Raja Alaska segar yang manis di atas lapisan alpukat.",
            "Hias dengan irisan lobak semangka, lalu rintiki dengan minyak herba klorofil segar sebelum disajikan."
        )
    )
)

fun getPremiumFoodForId(id: String, username: String?, dateStr: String): PremiumFood {
    val seed = (username ?: "User").hashCode() + id.hashCode() + dateStr.hashCode()
    val index = Math.abs(seed) % premiumFoodsPool.size
    return premiumFoodsPool[index]
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardsCatalogScreen(
    viewModel: EisseryViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val user by viewModel.user.collectAsState()
    val transactions by viewModel.transactions.collectAsState(initial = emptyList())
    val todayStr = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
    var selectedReward by remember { mutableStateOf<CatalogItem?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var claimedItemName by remember { mutableStateOf("") }
    var viewingRecipeFood by remember { mutableStateOf<PremiumFood?>(null) }

    val rewards = listOf(
        CatalogItem(
            id = "bronze_gourmet",
            name = "Bronze Gourmet Surprise",
            description = "Tukarkan 150 poin untuk membuka hidangan sehat tingkat Bronze yang acak, lezat, dan mewah!",
            cost = 150,
            emoji = "❓",
            category = "Bronze"
        ),
        CatalogItem(
            id = "silver_gourmet",
            name = "Silver Gourmet Surprise",
            description = "Tukarkan 300 poin untuk membuka hidangan sehat tingkat Silver yang acak, lezat, dan mewah!",
            cost = 300,
            emoji = "❓",
            category = "Silver"
        ),
        CatalogItem(
            id = "gold_gourmet",
            name = "Gold Gourmet Surprise",
            description = "Tukarkan 500 poin untuk membuka hidangan sehat tingkat Gold yang acak, lezat, dan mewah!",
            cost = 500,
            emoji = "❓",
            category = "Gold"
        ),
        CatalogItem(
            id = "platinum_gourmet",
            name = "Platinum Gourmet Surprise",
            description = "Tukarkan 800 poin untuk membuka hidangan sehat tingkat Platinum yang acak, lezat, dan mewah!",
            cost = 800,
            emoji = "❓",
            category = "Platinum"
        ),
        CatalogItem(
            id = "royal_gourmet",
            name = "Royal VIP Surprise Feast",
            description = "Tukarkan 1500 poin untuk membuka hidangan sehat tingkat Royal yang acak, lezat, dan mewah kelas dunia!",
            cost = 1500,
            emoji = "❓",
            category = "Royal VIP"
        )
    )

    Scaffold(
        modifier = modifier.fillMaxSize().testTag("rewards_catalog_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "REWARDS CATALOG",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                            color = DeepCurrent
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("catalog_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = DeepCurrent
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(1), // Use beautiful full-width items for rich description reading
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Prominent Points Header in Citron Yellow (#FED43A)
            item(span = { GridItemSpan(maxLineSpan) }) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                        .testTag("catalog_points_header"),
                    shape = RoundedCornerShape(20.dp),
                    color = CitronYellow,
                    shadowElevation = 4.dp
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Stars,
                                    contentDescription = null,
                                    tint = DeepCurrent,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "YOUR LOYALTY POINTS",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DeepCurrent.copy(alpha = 0.6f),
                                        letterSpacing = 1.sp
                                    )
                                )
                            }

                            Surface(
                                color = DeepCurrent,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "STUDENT VALUE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = CitronYellow,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "${user?.totalPoints ?: 0} pts",
                            style = MaterialTheme.typography.displaySmall.copy(
                                fontWeight = FontWeight.Black,
                                color = DeepCurrent
                            ),
                            modifier = Modifier.testTag("catalog_points_text")
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Tukarkan poin loyalitas Anda di bawah ini untuk membuka menu makanan sehat kustom yang super lezat, sehat, dan mewah!",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = DeepCurrent.copy(alpha = 0.8f)
                            )
                        )
                    }
                }
            }

            // Section label
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    text = "AVAILABLE REDEMPTIONS",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = DeepCurrent.copy(alpha = 0.6f),
                        letterSpacing = 1.sp
                    ),
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            // Rewards lists
            items(rewards, key = { it.id }) { reward ->
                val points = user?.totalPoints ?: 0
                val canAfford = points >= reward.cost
                val isUnlocked = remember(transactions, reward.id, reward.name, todayStr) {
                    transactions.any { transaction ->
                        val transactionDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(transaction.timestamp))
                        transaction.isRedemption && transactionDate == todayStr && (
                            transaction.productName.contains(reward.name, ignoreCase = true) ||
                            transaction.productCode.contains(reward.id, ignoreCase = true)
                        )
                    }
                }

                val premiumFood = remember(reward.id, user?.name, todayStr) {
                    getPremiumFoodForId(reward.id, user?.name, todayStr)
                }

                val displayEmoji = if (isUnlocked) premiumFood.emoji else "❓"
                val displayName = if (isUnlocked) "✨ ${premiumFood.name}" else "${reward.name} (Terkunci 🔒)"
                val displayDescription = if (isUnlocked) {
                    premiumFood.description
                } else {
                    "Tukarkan ${reward.cost} poin untuk membuka hidangan sehat premium misteri tingkat ${reward.category}."
                }
                val displayCategory = if (isUnlocked) premiumFood.category else "TERKUNCI 🔒"

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer { alpha = if (isUnlocked) 1f else 0.75f }
                        .testTag("reward_item_${reward.id}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isUnlocked) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (isUnlocked) 4.dp else 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Emoji block
                            Surface(
                                color = if (isUnlocked) CitronYellow.copy(alpha = 0.25f) else Color.LightGray.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.size(54.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(text = displayEmoji, fontSize = 28.sp)
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Surface(
                                    color = if (isUnlocked) BlueTurquoise.copy(alpha = 0.15f) else DeepCurrent.copy(alpha = 0.08f),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.align(Alignment.Start)
                                ) {
                                    Text(
                                        text = displayCategory.uppercase(),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (isUnlocked) BlueTurquoise else DeepCurrent.copy(alpha = 0.6f),
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = displayName,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isUnlocked) DeepCurrent else DeepCurrent.copy(alpha = 0.6f)
                                    )
                                )
                            }

                            // Cost badge
                            Surface(
                                color = if (isUnlocked) BlueTurquoise.copy(alpha = 0.2f) else if (canAfford) CitronYellow else Color.LightGray.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = if (isUnlocked) "TERBUKA" else "${reward.cost} pts",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        color = if (isUnlocked) BlueTurquoise else DeepCurrent
                                    ),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = displayDescription,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (isUnlocked) DeepCurrent else DeepCurrent.copy(alpha = 0.5f),
                                lineHeight = 16.sp,
                                fontWeight = if (isUnlocked) FontWeight.Medium else FontWeight.Normal
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        if (isUnlocked) {
                            Column {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    color = BlueTurquoise.copy(alpha = 0.12f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = BlueTurquoise,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Menu Terbuka & Siap Dinikmati!",
                                            style = MaterialTheme.typography.labelLarge.copy(
                                                color = BlueTurquoise,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { viewingRecipeFood = premiumFood },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(42.dp)
                                        .testTag("view_recipe_button_${reward.id}"),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = BlueTurquoise,
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocalActivity,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Lihat Resep Masakan 🍳",
                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        } else {
                            Button(
                                onClick = { selectedReward = reward },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(42.dp)
                                    .testTag("claim_button_${reward.id}"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (canAfford) DeepCurrent else Color.LightGray.copy(alpha = 0.2f),
                                    contentColor = if (canAfford) Color.White else DeepCurrent.copy(alpha = 0.4f)
                                ),
                                shape = RoundedCornerShape(10.dp),
                                enabled = canAfford
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CardGiftcard,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (canAfford) "Buka Hidangan Premium" else "Poin Tidak Cukup",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }
            }

            // Footer gap
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // Confirmation Dialog
    selectedReward?.let { reward ->
        AlertDialog(
            onDismissRequest = { selectedReward = null },
            title = {
                Text(
                    text = "Konfirmasi Penukaran 🎁",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = DeepCurrent)
                )
            },
            text = {
                Text(
                    text = "Apakah Anda yakin ingin menukarkan ${reward.cost} poin loyalitas untuk membuka hidangan sehat premium dari '${reward.name}'? Poin Anda akan dikurangi secara otomatis.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = DeepCurrent.copy(alpha = 0.7f))
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.redeemRewardItem(reward.name, reward.cost) {
                            val premiumFood = getPremiumFoodForId(reward.id, user?.name, todayStr)
                            claimedItemName = premiumFood.name
                            showSuccessDialog = true
                        }
                        selectedReward = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BlueTurquoise)
                ) {
                    Text("Tukarkan Sekarang")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedReward = null }) {
                    Text("Batal", color = DeepCurrent.copy(alpha = 0.6f))
                }
            }
        )
    }

    // Success Reward Claims Dialog
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = "Hidangan Berhasil Dibuka! 🎉",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = DeepCurrent),
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
                    text = "Selamat! Anda telah berhasil membuka hidangan super mewah dan sehat:\n\n✨ $claimedItemName ✨\n\nHidangan ini sekarang terbuka secara permanen di daftar penukaran Anda. Nikmati resep kuliner eksklusif dan lezat ini!",
                    style = MaterialTheme.typography.bodyMedium.copy(color = DeepCurrent.copy(alpha = 0.7f)),
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = { showSuccessDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepCurrent)
                ) {
                    Text("Luar Biasa!")
                }
            }
        )
    }

    // Recipe Detail Dialog
    viewingRecipeFood?.let { food ->
        AlertDialog(
            onDismissRequest = { viewingRecipeFood = null },
            icon = {
                Text(text = food.emoji, fontSize = 48.sp)
            },
            title = {
                Text(
                    text = food.name,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = DeepCurrent
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = food.description,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = DeepCurrent.copy(alpha = 0.8f),
                            fontStyle = FontStyle.Italic
                        )
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(DeepCurrent.copy(alpha = 0.1f))
                    )

                    Text(
                        text = "Bahan-Bahan 🛒",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = BlueTurquoise
                        )
                    )

                    food.ingredients.forEach { ingredient ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(CitronYellow)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = ingredient,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = DeepCurrent
                                )
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(DeepCurrent.copy(alpha = 0.1f))
                    )

                    Text(
                        text = "Langkah Pembuatan 🍳",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = BlueTurquoise
                        )
                    )

                    food.instructions.forEachIndexed { index, step ->
                        Row(
                            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "${index + 1}.",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = BlueTurquoise
                                )
                            )
                            Text(
                                text = step,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = DeepCurrent
                                )
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewingRecipeFood = null },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepCurrent),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Tutup Resep")
                }
            }
        )
    }
}
