package com.example.mas_cake1.data

import com.example.mas_cake1.R
import com.example.mas_cake1.data.CakeCategory.BIRTHDAY
import com.example.mas_cake1.data.CakeCategory.USUAL
import com.example.mas_cake1.data.CakeCategory.WEDDING
import com.example.mas_cake1.data.CakeTag.BESTSELLER
import com.example.mas_cake1.data.CakeTag.CHOCOLATE
import com.example.mas_cake1.data.CakeTag.FRUIT

/**
 * The single source of truth for the bakery menu.
 * (Before this, the same list was copied into 5 different screens with different prices.)
 */
object CakeCatalog {

    private val drawables = mapOf(
        "cake_1wed" to R.drawable.cake_1wed,
        "cake_2wed" to R.drawable.cake_2wed,
        "cake_3wed" to R.drawable.cake_3wed,
        "cake_4wed" to R.drawable.cake_4wed,
        "cake_5wed" to R.drawable.cake_5wed,
        "cake_1birth" to R.drawable.cake_1birth,
        "cake_2birth" to R.drawable.cake_2birth,
        "cake_3birth" to R.drawable.cake_3birth,
        "cake_4birth" to R.drawable.cake_4birth,
        "cake_5birth" to R.drawable.cake_5birth,
        "cake_usual1" to R.drawable.cake_usual1,
        "cake_usual2" to R.drawable.cake_usual2,
        "cake_usual3" to R.drawable.cake_usual3,
        "cake_usual4" to R.drawable.cake_usual4,
        "cake_usual5" to R.drawable.cake_usual5,
        "cake_usual6" to R.drawable.cake_usual6,
        "cake_usual7" to R.drawable.cake_usual7,
        "cake_usual8" to R.drawable.cake_usual8,
        "cake_usual9" to R.drawable.cake_usual9,
        "cake_usual10" to R.drawable.cake_usual10
    )

    fun drawableFor(key: String): Int? = drawables[key]

    fun categoryImage(category: CakeCategory): Int = when (category) {
        WEDDING -> R.drawable.wedding_main_page
        BIRTHDAY -> R.drawable.birthday_main_page
        USUAL -> R.drawable.usualcake_main_page
    }

    val defaultCakes: List<Cake> = listOf(
        // --- WEDDING CAKES ---
        Cake(
            "royal_fruitcake", "Traditional Royal Fruitcake", "Королевский фруктовый торт", WEDDING, 3500, "cake_1wed",
            descEn = "A towering, multi-tiered dark fruitcake covered in smooth marzipan and royal icing. Dense, boozy, and built to last.\n\nIngredients: Dried fruits, brandy or rum, butter, dark brown sugar, eggs, flour, mixed spice, almonds, marzipan, royal icing.",
            descRu = "Многоярусный темный фруктовый торт, покрытый гладким марципаном и королевской глазурью. Плотный, с добавлением алкоголя.\n\nИнгредиенты: Сухофрукты, бренди или ром, масло, темно-коричневый сахар, яйца, мука, смесь специй, миндаль, марципан, королевская глазурь.",
            tags = setOf(FRUIT), onPromo = true
        ),
        Cake(
            "lemon_elderflower", "Lemon & Elderflower Sponge", "Лимонный бисквит с бузиной", WEDDING, 2800, "cake_2wed",
            descEn = "A light, modern spring wedding cake — fluffy lemon sponge layers brushed with elderflower syrup, filled with lemon curd.\n\nIngredients: Lemons, elderflower cordial, butter, caster sugar, eggs, self-raising flour, lemon curd, egg whites, icing sugar, edible fresh flowers.",
            descRu = "Легкий современный весенний свадебный торт — воздушные коржи с лимоном, пропитанные сиропом из бузины, с лимонным курдом.\n\nИнгредиенты: Лимоны, сироп бузины, масло, сахар, яйца, мука, лимонный курд, яичные белки, сахарная пудра, съедобные свежие цветы.",
            tags = setOf(FRUIT)
        ),
        Cake(
            "yellow_bavarian", "Yellow Sponge with Bavarian Cream", "Бисквит с баварским кремом", WEDDING, 3000, "cake_3wed",
            descEn = "A retro, showy six-tier yellow sponge cake with a rich kirsch-soaked filling — a mid-century glamour cake.\n\nIngredients: Yellow sponge, apricot marmalade, kirsch, Bavarian cream, fondant icing, marzipan, royal icing.",
            descRu = "Ретро-торт из шести ярусов желтого бисквита с насыщенной начинкой, пропитанной киршем — гламурный торт в стиле середины прошлого века.\n\nИнгредиенты: Желтый бисквит, абрикосовый мармелад, кирш, баварский крем, помадка, марципан, королевская глазурь.",
            tags = setOf(FRUIT)
        ),
        Cake(
            "grooms_chocolate", "Chocolate Biscuit Groom's Cake", "Шоколадный торт жениха", WEDDING, 1800, "cake_4wed",
            descEn = "A rich, no-bake-style chocolate cake made with crushed biscuits and dark chocolate. Fudgy and crunchy.\n\nIngredients: Dark chocolate, butter, sugar, golden syrup, crushed tea biscuits, cream, eggs, cocoa powder.",
            descRu = "Насыщенный шоколадный торт без выпечки из измельченного печенья и темного шоколада. Очень шоколадный и хрустящий.\n\nИнгредиенты: Темный шоколад, масло, сахар, золотой сироп, измельченное чайное печенье, сливки, яйца, какао-порошок.",
            tags = setOf(CHOCOLATE)
        ),
        Cake(
            "white_vanilla", "Classic White Vanilla Buttercream", "Классический ванильный торт", WEDDING, 1600, "cake_5wed",
            descEn = "The quintessential white wedding cake — light vanilla sponge layers filled with vanilla buttercream or raspberry jam. Elegant and timeless.\n\nIngredients: Vanilla sponge, vanilla buttercream, raspberry jam, white fondant, sugar pearls.",
            descRu = "Классический белый свадебный торт — светлые ванильные коржи с ванильным кремом или малиновым джемом. Элегантный и вне времени.\n\nИнгредиенты: Ванильный бисквит, ванильный крем, малиновый джем, белая помадка, сахарный жемчуг.",
            tags = setOf(FRUIT)
        ),

        // --- BIRTHDAY CAKES ---
        Cake(
            "medovik", "Honey Cake (Medovik)", "Медовик", BIRTHDAY, 3300, "cake_1birth",
            descEn = "An absolute hit and a nostalgic classic. It is a cake made of thin honey layers with thick sour cream or custard.\n\nIngredients: Flour, honey, eggs, sugar, soda, sour cream (or boiled condensed milk) for the cream.",
            descRu = "Абсолютный хит и ностальгическая классика. Это торт из тонких медовых коржей с густым сметанным или заварным кремом.\n\nИнгредиенты: Мука, мёд, яйца, сахар, сода, сметана (или варёная сгущёнка) для крема.",
            tags = setOf(BESTSELLER), onPromo = true
        ),
        Cake(
            "napoleon", "Napoleon", "Наполеон", BIRTHDAY, 2300, "cake_2birth",
            descEn = "The second most popular Soviet classic. It is a puff pastry cake with a large number of thin layers and delicate custard.\n\nIngredients: Flour, butter, eggs, water/vinegar for the dough; milk, yolks, sugar, flour for the custard.",
            descRu = "Вторая по популярности советская классика. Это слоёный торт с большим количеством тонких коржей и нежным заварным кремом.\n\nИнгредиенты: Мука, сливочное масло, яйца, вода/уксус для теста; молоко, желтки, сахар, мука для заварного крема.",
            tags = setOf(BESTSELLER), onPromo = true
        ),
        Cake(
            "prague_birthday", "Chocolate Cake (Prague)", "Шоколадный торт «Прага»", BIRTHDAY, 2400, "cake_3birth",
            descEn = "A classic chocolate sponge with cream and glaze — the favourite of every chocolate lover.\n\nIngredients: Cocoa, flour, eggs, sugar, condensed milk, butter, chocolate for glaze, apricot jam.",
            descRu = "Классический шоколадный бисквит с кремом и глазурью — любимец всех шоколадных гурманов.\n\nИнгредиенты: Какао, мука, яйца, сахар, сгущённое молоко, масло, шоколад для глазури, абрикосовый джем.",
            tags = setOf(CHOCOLATE), onPromo = true
        ),
        Cake(
            "cheesecake", "Cheesecake", "Чизкейк", BIRTHDAY, 2100, "cake_4birth",
            descEn = "Third place in popularity. In Russia, both classic delicate cheesecake on a shortbread base and cottage cheese variations are popular.\n\nIngredients: Cream cheese (or cottage cheese), cream, eggs, sugar, shortbread base (flour, butter, sugar).",
            descRu = "Третье место по популярности. В России прижился как классический нежный чизкейк на песочной основе, так и творожные варианты.\n\nИнгредиенты: Сливочный сыр (или творог), сливки, яйца, сахар, песочная основа (мука, масло, сахар).",
            tags = setOf(BESTSELLER), onPromo = true
        ),
        Cake(
            "bento", "Bento Cake", "Бенто-торт", BIRTHDAY, 2278, "cake_5birth",
            descEn = "Not a flavour, but a format that blew up the Russian market: a compact cake (1–2 servings), often ordered just 'for yourself'. Price is per piece.\n\nIngredients: Depends on the filling (often sponge cake, cream cheese, berry fillings).",
            descRu = "Это не вкус, а формат, который взорвал российский рынок: маленький торт в контейнере (на 1–2 порции), который часто заказывают просто «для себя». Цена за штуку.\n\nИнгредиенты: Зависят от начинки (часто бисквит, крем-чиз, ягодные начинки).",
            isPerPiece = true
        ),

        // --- USUAL CAKES ---
        Cake(
            "ptichye_moloko", "Bird's Milk (Ptichye Moloko)", "Птичье молоко", USUAL, 2600, "cake_usual1",
            descEn = "A Soviet legend created in 1978 in the 'Prague' restaurant in Moscow. Thick airy layers of delicate soufflé separated by thin sponge layers, topped with chocolate glaze.",
            descRu = "Советская легенда, созданная в 1978 году в ресторане «Прага» в Москве. Толстые воздушные слои нежного суфле, разделённые тонкими бисквитными коржами, сверху — шоколадная глазурь.",
            tags = setOf(BESTSELLER)
        ),
        Cake(
            "smetannik", "Sour Cream Cake (Smetannik)", "Сметанник", USUAL, 535, "cake_usual2",
            descEn = "A traditional layered cake made of flour, sugar, eggs and honey with thick sour cream (often with cream cheese). Assembled in a deep form and left to soak overnight.",
            descRu = "Традиционный слоёный торт из муки, сахара, яиц и мёда с густым кремом из сметаны (часто с добавлением сливочного сыра). Собирается в глубокой форме и настаивается ночь в холодильнике."
        ),
        Cake(
            "nevsky", "Nevsky Pie (Nevsky Pirog)", "Невский пирог", USUAL, 226, "cake_usual3",
            descEn = "A Soviet classic from St. Petersburg. Soft yeast dough soaked in cognac-sugar syrup, with a layer of buttercream on condensed milk.",
            descRu = "Советская классика из Санкт-Петербурга. Мягкое дрожжевое тесто, пропитанное коньячно-сахарным сиропом, с прослойкой из масляного крема на сгущённом молоке."
        ),
        Cake(
            "mishka", "Bear in the North (Mishka)", "Мишка на севере", USUAL, 2950, "cake_usual4",
            descEn = "Inspired by the Soviet candy of the same name. Sour cream layers with cocoa, layered with sour cream and nut cream, decorated with crushed nuts.",
            descRu = "Торт, вдохновлённый одноимённой советской конфетой. Коржи на сметане с какао, прослойка из сметанно-орехового крема, украшение из дробленых орехов.",
            tags = setOf(CHOCOLATE)
        ),
        Cake(
            "prague_classic", "Prague (Classic)", "Прага", USUAL, 4200, "cake_usual5",
            descEn = "A nostalgic Soviet chocolate cake: chocolate sponge layers, buttercream and apricot jam, covered with chocolate glaze.",
            descRu = "Ностальгический советский шоколадный торт: шоколадные бисквитные коржи, масляный крем и абрикосовый джем, покрытие — шоколадная глазурь.",
            tags = setOf(CHOCOLATE, BESTSELLER)
        ),
        Cake(
            "tiramisu", "Tiramisu", "Тирамису", USUAL, 3345, "cake_usual6",
            descEn = "A world-famous no-bake dessert: savoiardi biscuits soaked in espresso and whipped mascarpone cream, topped with cocoa powder.",
            descRu = "Всемирно известный десерт без выпечки: печенье савоярди, пропитанное эспрессо, и взбитый крем из маскарпоне, сверху — какао-порошок.",
            tags = setOf(BESTSELLER)
        ),
        Cake(
            "sachertorte", "Sachertorte", "Захерторт", USUAL, 1120, "cake_usual7",
            descEn = "A dense chocolate cake with a thin layer of apricot jam, covered in dark chocolate glaze. Created in 1832 by Franz Sacher.",
            descRu = "Плотный шоколадный торт с тонким слоем абрикосового джема, покрытый тёмной шоколадной глазурью. Создан в 1832 году Францем Захером.",
            tags = setOf(CHOCOLATE), available = false, stock = 0
        ),
        Cake(
            "red_velvet", "Red Velvet", "Красный бархат", USUAL, 924, "cake_usual8",
            descEn = "A crimson sponge with a light chocolate flavour, layered with cream cheese frosting. One of the most recognisable cakes in America.",
            descRu = "Бисквит насыщенного красного цвета с лёгким шоколадным вкусом, прослоенный кремом из сливочного сыра. Один из самых узнаваемых тортов Америки.",
            tags = setOf(CHOCOLATE)
        ),
        Cake(
            "opera", "Opéra", "Опера", USUAL, 3500, "cake_usual9",
            descEn = "A classic of French pastry. Layers of almond sponge soaked in coffee syrup, coffee buttercream and chocolate ganache.",
            descRu = "Классика французского кондитерского искусства. Слои миндального бисквита, пропитанного кофейным сиропом, кофейный масляный крем и шоколадный ганаш.",
            tags = setOf(CHOCOLATE), stock = 2
        ),
        Cake(
            "black_forest", "Black Forest", "Чёрный лес", USUAL, 2567, "cake_usual10",
            descEn = "A layered chocolate cake with cherries and cherry brandy (kirsch), decorated with whipped cream and cherries. Originated in Germany in the early 1930s.",
            descRu = "Слоёный шоколадный торт с вишней и вишнёвым бренди (кирш), украшенный взбитыми сливками и вишней. Появился в Германии в начале 1930-х годов.",
            tags = setOf(CHOCOLATE, FRUIT)
        )
    )
}
