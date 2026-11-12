package com.example.mas_cake1.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.mas_cake1.R
import com.example.mas_cake1.ui.theme.ChocolateBrown
import com.example.mas_cake1.ui.theme.CreamWhite

data class CakeDetailData(
    val imageRes: Int,
    val price1kg: Int,
    val price15kg: Int,
    val price2kg: Int,
    val descEn: String,
    val descRu: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    cakeName: String,
    onBackClicked: () -> Unit,
    onAddToCart: (String, String, Int) -> Unit,
    isEnglish: Boolean
) {
    var weight by remember { mutableStateOf("1 кг") }

    val cakeData = mapOf(
        // --- WEDDING CAKES ---
        "Traditional Royal Fruitcake" to CakeDetailData(
            R.drawable.cake_1wed, 3500, 5250, 7000,
            "A towering, multi-tiered dark fruitcake covered in smooth marzipan and royal icing. Dense, boozy, and built to last.\n\nIngredients: Dried fruits, brandy or rum, butter, dark brown sugar, eggs, flour, mixed spice, almonds, marzipan, royal icing.",
            "Многоярусный темный фруктовый торт, покрытый гладким марципаном и королевской глазурью. Плотный, с добавлением алкоголя.\n\nИнгредиенты: Сухофрукты, бренди или ром, масло, темно-коричневый сахар, яйца, мука, смесь специй, миндаль, марципан, королевская глазурь."
        ),
        "Lemon & Elderflower Sponge" to CakeDetailData(
            R.drawable.cake_2wed, 2800, 4200, 5600,
            "A light, modern spring wedding cake — fluffy lemon sponge layers brushed with elderflower syrup, filled with lemon curd.\n\nIngredients: Lemons, elderflower cordial, butter, caster sugar, eggs, self-raising flour, lemon curd, egg whites, icing sugar, edible fresh flowers.",
            "Легкий современный весенний свадебный торт — воздушные коржи с лимоном, пропитанные сиропом из бузины, с лимонным курдом.\n\nИнгредиенты: Лимоны, сироп бузины, масло, сахар, яйца, мука, лимонный курд, яичные белки, сахарная пудра, съедобные свежие цветы."
        ),
        "Yellow Sponge with Bavarian Cream" to CakeDetailData(
            R.drawable.cake_3wed, 3000, 4500, 6000,
            "A retro, showy six-tier yellow sponge cake with a rich kirsch-soaked filling — a mid-century glamour cake.\n\nIngredients: Yellow sponge, apricot marmalade, kirsch, Bavarian cream, fondant icing, marzipan, royal icing.",
            "Ретро-торт из шести ярусов желтого бисквита с насыщенной начинкой, пропитанной киршем — гламурный торт в стиле середины прошлого века.\n\nИнгредиенты: Желтый бисквит, абрикосовый мармелад, кирш, баварский крем, помадка, марципан, королевская глазурь."
        ),
        "Chocolate Biscuit Groom's Cake" to CakeDetailData(
            R.drawable.cake_4wed, 1800, 2700, 3600,
            "A rich, no-bake-style chocolate cake made with crushed biscuits and dark chocolate. Fudgy and crunchy.\n\nIngredients: Dark chocolate, butter, sugar, golden syrup, crushed tea biscuits, cream, eggs, cocoa powder.",
            "Насыщенный шоколадный торт без выпечки из измельченного печенья и темного шоколада. Очень шоколадный и хрустящий.\n\nИнгредиенты: Темный шоколад, масло, сахар, золотой сироп, измельченное чайное печенье, сливки, яйца, какао-порошок."
        ),
        "Classic White Vanilla Buttercream" to CakeDetailData(
            R.drawable.cake_5wed, 1600, 2400, 3200,
            "The quintessential white wedding cake — light vanilla sponge layers filled with vanilla buttercream or raspberry jam. Elegant and timeless.\n\nIngredients: Vanilla sponge, vanilla buttercream, raspberry jam, white fondant, sugar pearls.",
            "Классический белый свадебный торт — светлые ванильные коржи с ванильным кремом или малиновым джемом. Элегантный и вне времени.\n\nИнгредиенты: Ванильный бисквит, ванильный крем, малиновый джем, белая помадка, сахарный жемчуг."
        ),

        // --- BIRTHDAY CAKES ---
        "Honey Cake (Medovik)" to CakeDetailData(
            R.drawable.cake_1birth, 3300, 4950, 6600,
            "An absolute hit and a nostalgic classic. It is a cake made of thin honey layers with thick sour cream or custard.\n\nIngredients: Flour, honey, eggs, sugar, soda, sour cream (or boiled condensed milk) for the cream.",
            "Абсолютный хит и ностальгическая классика. Это торт из тонких медовых коржей с густым сметанным или заварным кремом.\n\nИнгредиенты: Мука, мёд, яйца, сахар, сода, сметана (или варёная сгущёнка) для крема."
        ),
        "Napoleon" to CakeDetailData(
            R.drawable.cake_2birth, 2300, 3450, 4600,
            "The second most popular Soviet classic. It is a puff pastry cake with a large number of thin layers and delicate custard.\n\nIngredients: Flour, butter, eggs, water/vinegar for the dough; milk, yolks, sugar, flour for the custard.",
            "Вторая по популярности советская классика. Это слоёный торт с большим количеством тонких коржей и нежным заварным кремом.\n\nИнгредиенты: Мука, сливочное масло, яйца, вода/уксус для теста; молоко, желтки, сахар, мука для заварного крема."
        ),
        "Chocolate Cake (Prague)" to CakeDetailData(
            R.drawable.cake_3birth, 2400, 3600, 4800,
            "The largest group by preference. This includes 'Prague' (a classic chocolate sponge with cream and glaze) and 'Red Velvet'.\n\nIngredients (for Prague): Cocoa, flour, eggs, sugar, condensed milk, butter, chocolate for glaze, apricot jam.",
            "Самая большая группа по предпочтениям. Сюда можно отнести и «Прагу» (классический шоколадный бисквит с кремом и глазурью), и «Красный бархат».\n\nИнгредиенты (для «Праги»): Какао, мука, яйца, сахар, сгущённое молоко, масло, шоколад для глазури, абрикосовый джем."
        ),
        "Cheesecake" to CakeDetailData(
            R.drawable.cake_4birth, 2100, 3150, 4200,
            "Third place in popularity. In Russia, both classic delicate cheesecake on a shortbread base and cottage cheese variations are popular.\n\nIngredients: Cream cheese (or cottage cheese), cream, eggs, sugar, shortbread base (flour, butter, sugar).",
            "Третье место по популярности. В России прижился как классический нежный чизкейк на песочной основе, так и творожные варианты.\n\nИнгредиенты: Сливочный сыр (или творог), сливки, яйца, сахар, песочная основа (мука, масло, сахар)."
        ),
        "Bento Cake" to CakeDetailData(
            R.drawable.cake_5birth, 2278, 2278, 2278,
            "This is not a flavor, but a format that blew up the Russian market. These are compact cakes (for 1-2 servings), often ordered just 'for yourself'. (Price is per piece).\n\nIngredients: Depends on the filling (often sponge cake, cream cheese, berry fillings are used).",
            "Это не вкус, а формат, который взорвал российский рынок. Это маленький торт в контейнере (на 1-2 порции), который часто заказывают просто «для себя». (Цена за штуку).\n\nИнгредиенты: Зависят от начинки (часто используются бисквит, крем-чиз, ягодные начинки)."
        ),

        // --- USUAL CAKES ---
        "Птичье молоко (Ptichye Moloko)" to CakeDetailData(
            R.drawable.cake_usual1, 2600, 3900, 5200,
            "A Soviet legend created in 1978 in the 'Prague' restaurant in Moscow. Famous for its 'inverted' proportion: thick airy layers of delicate soufflé separated by thin sponge layers, topped with chocolate glaze. (Avg. price ~2600 ₽/kg).",
            "Советская легенда, созданная в 1978 году в ресторане «Прага» в Москве. Знаменит «перевёрнутой» пропорцией: толстые воздушные слои нежного суфле, разделённые тонкими бисквитными коржами, сверху — шоколадная глазурь."
        ),
        "Сметанник (Smetannik)" to CakeDetailData(
            R.drawable.cake_usual2, 535, 803, 1070,
            "A traditional layered cake made of flour, sugar, eggs, and honey with thick sour cream (often with cream cheese). Gathered in a deep form and infused overnight. (Supermarket reference price ~535 ₽/kg).",
            "Традиционный слоёный торт из муки, сахара, яиц и мёда с густым кремом из сметаны (часто с добавлением сливочного сыра). Собирается в глубокой форме и настаивается ночь в холодильнике."
        ),
        "Невский пирог (Nevsky Pirog)" to CakeDetailData(
            R.drawable.cake_usual3, 226, 339, 452,
            "A Soviet classic from St. Petersburg. Made from soft yeast dough soaked in cognac-sugar syrup, with a layer of buttercream on condensed milk. (Limited price data ~226 ₽/kg).",
            "Советская классика из Санкт-Петербурга. Готовится из мягкого дрожжевого теста, пропитанного коньячно-сахарным сиропом, с прослойкой из масляного крема на сгущённом молоке."
        ),
        "Мишка на севере (Mishka na Severe)" to CakeDetailData(
            R.drawable.cake_usual4, 2950, 4425, 5900,
            "A cake inspired by the Soviet candy of the same name. Sour cream layers with cocoa and butter, layered with sour cream and nut cream, decorated with crushed nuts. (Avg. price ~2950 ₽/kg).",
            "Торт, вдохновлённый одноимённой советской конфетой. Коржи на сметане с какао и маслом, прослойка из сметанно-орехового крема, украшение из дробленых орехов."
        ),
        "Прага (Prague)" to CakeDetailData(
            R.drawable.cake_usual5, 4200, 6300, 8400,
            "A nostalgic Soviet chocolate cake: chocolate sponge layers, buttercream and apricot jam, covered with chocolate glaze. (Avg. custom shop price ~4200 ₽/kg).",
            "Ностальгический советский шоколадный торт: шоколадные бисквитные коржи, масляный крем и абрикосовый джем, покрытие — шоколадная глазурь."
        ),
        "Тирамису (Tiramisu)" to CakeDetailData(
            R.drawable.cake_usual6, 3345, 5018, 6690,
            "A world-famous no-bake dessert: layers of savoiardi biscuits soaked in espresso and whipped mascarpone cream, topped with cocoa powder. (Avg. price ~3345 ₽/kg).",
            "Всемирно известный десерт без выпечки: слои печенья савоярди, пропитанного эспрессо, и взбитого крема из маскарпоне, сверху — какао-порошок."
        ),
        "Захерторт (Sachertorte)" to CakeDetailData(
            R.drawable.cake_usual7, 1120, 1680, 2240,
            "A dense chocolate cake with a thin layer of apricot jam, covered in dark chocolate glaze. Created in 1832 by Franz Sacher. (Supermarket reference price ~1120 ₽/kg).",
            "Плотный шоколадный торт с тонким слоем абрикосового джема, покрытый тёмной шоколадной глазурью. Создан в 1832 году Францем Захером."
        ),
        "Красный бархат (Red Velvet)" to CakeDetailData(
            R.drawable.cake_usual8, 924, 1386, 1848,
            "A crimson-colored sponge with a chocolate flavor, layered with cream cheese frosting. One of the most recognizable cakes in America. (Supermarket reference price ~924 ₽/kg).",
            "Бисквит характерного crimson-цвета с шоколадным вкусом, прослоенный кремом из сливочного сыра. Один из самых узнаваемых и часто заказываемых тортов Америки."
        ),
        "Опера (Opéra)" to CakeDetailData(
            R.drawable.cake_usual9, 3500, 5250, 7000,
            "A classic of French pastry art. Layers of almond sponge soaked in coffee syrup, coffee buttercream, and chocolate ganache. (Avg. price ~3500 ₽/kg).",
            "Классика французского кондитерского искусства. Слои миндального бисквита, пропитанного кофейным сиропом, кофейный масляный крем и шоколадный ганаш."
        ),
        "Чёрный лес (Black Forest)" to CakeDetailData(
            R.drawable.cake_usual10, 2567, 3850, 5134,
            "A layered chocolate cake with cherries and cherry brandy (kirsch), decorated with whipped cream and cherries. Originated in Germany in the early 1930s. (Avg. price ~2567 ₽/kg).",
            "Слоёный шоколадный торт с вишней и вишнёвым бренди (кирш), украшенный взбитыми сливками и вишней. Появился в Германии в начале 1930-х годов."
        )
    )

    val currentCake = cakeData[cakeName]
    val imageRes = currentCake?.imageRes ?: R.drawable.cake_usual1

    val currentPrice = when (weight) {
        "1.5 кг" -> currentCake?.price15kg ?: 0
        "2 кг" -> currentCake?.price2kg ?: 0
        else -> currentCake?.price1kg ?: 0
    }

    val description = if (isEnglish) currentCake?.descEn ?: "Description coming soon." else currentCake?.descRu ?: "Описание скоро появится."
    val priceLabel = if (isEnglish) "Price: $currentPrice ₽" else "Цена: $currentPrice ₽"
    val weightLabel = if (isEnglish) "Select Weight:" else "Выберите вес:"
    val addToCartLabel = if (isEnglish) "Add to Cart — $currentPrice ₽" else "В корзину — $currentPrice ₽"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEnglish) "Details" else "Детали", color = ChocolateBrown) },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = ChocolateBrown)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CreamWhite)
            )
        },
        containerColor = CreamWhite
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize().verticalScroll(rememberScrollState())
        ) {
            Text(cakeName, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = ChocolateBrown)
            Text(priceLabel, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = ChocolateBrown, modifier = Modifier.padding(top = 4.dp, bottom = 16.dp))

            Card(shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(4.dp), modifier = Modifier.fillMaxWidth().height(250.dp)) {
                AsyncImage(model = imageRes, contentDescription = cakeName, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = description, fontSize = 16.sp, color = Color.DarkGray, lineHeight = 24.sp)

            Spacer(modifier = Modifier.height(24.dp))
            Text(weightLabel, fontWeight = FontWeight.Bold, color = ChocolateBrown)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("1 кг", "1.5 кг", "2 кг").forEach { option ->
                    FilterChip(
                        selected = weight == option,
                        onClick = { weight = option },
                        label = { Text(option) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = ChocolateBrown, selectedLabelColor = Color.White)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { onAddToCart(cakeName, weight, currentPrice) },
                modifier = Modifier.fillMaxWidth().height(55.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ChocolateBrown)
            ) {
                Text(addToCartLabel, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}