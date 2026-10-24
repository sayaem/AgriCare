package com.example.data.local

object StarterCropLibrary {
    val starterArticles = listOf(
        GuideArticleEntity(
            id = "rice_blast",
            crop = "Rice",
            title = "Rice Blast",
            scientificName = "Magnaporthe oryzae",
            category = "Fungal Disease",
            description = "A common fungal disease that affects rice plants, especially during humid weather and excessive nitrogen application. It can reduce yield significantly if not controlled early.",
            symptoms = listOf(
                "Spindle-shaped lesions with grayish center and brown borders on leaves",
                "Neck rot causing panicles to fall over or stay empty (whitish panicles)",
                "Small brown specks enlarging into diamond-shaped spots",
                "Lesions merging and causing complete leaf drying under damp conditions"
            ),
            lookAlikes = listOf(
                "Brown spot (Bipolaris oryzae) - more circular and evenly dark brown",
                "Bacterial leaf blight - starts from leaf tips with wavy margins"
            ),
            management = listOf(
                "Drain standing water temporarily to reduce microclimate humidity",
                "Avoid late or excessive nitrogen top-dressing",
                "Apply recommended bio-fungicide or balanced foliar spray per local agricultural officer advice",
                "Burn or deeply bury infected stubble after harvest"
            ),
            prevention = listOf(
                "Use blast-resistant certified cultivars such as BRRI dhan-28 or BRRI dhan-89",
                "Treat seeds with hot water (53-54°C for 10-12 mins) or approved seed protectant",
                "Maintain optimal plant spacing to allow adequate ventilation and sunlight"
            ),
            sources = "Bangladesh Rice Research Institute (BRRI) Field Pathology Guide",
            reviewedAt = "Oct 2024",
            contentVersion = 1
        ),
        GuideArticleEntity(
            id = "rice_bacterial_blight",
            crop = "Rice",
            title = "Bacterial Leaf Blight",
            scientificName = "Xanthomonas oryzae pv. oryzae",
            category = "Bacterial Disease",
            description = "A widespread bacterial vascular disease favored by warm temperatures, high humidity, and monsoon rains, causing milky bacterial ooze and leaf wilting.",
            symptoms = listOf(
                "Water-soaked lesions starting at margins and tips of upper leaves",
                "Lesions enlarge with wavy margins, turning yellowish-white to straw-colored",
                "Bacterial droplets (amber beads) visible in morning dew on infected leaf edges",
                "Kresek phase causing complete wilting and lodging in young seedlings"
            ),
            lookAlikes = listOf(
                "Rice blast (diamond-shaped spots rather than wavy edge blighting)",
                "Drought stress or salt injury"
            ),
            management = listOf(
                "Avoid deep flooding of field; keep water level shallow (2-3 cm)",
                "Withhold nitrogen top-dressing until blighting subsides; apply potash (MOP) to improve resistance",
                "Do not work in wet fields to prevent spreading bacteria on clothes or tools"
            ),
            prevention = listOf(
                "Plant certified resistant varieties",
                "Ensure balanced N-P-K fertilization with sufficient potassium",
                "Maintain clean field borders free from wild grass hosts"
            ),
            sources = "Department of Agricultural Extension (DAE) Bangladesh",
            reviewedAt = "Sep 2024",
            contentVersion = 1
        ),
        GuideArticleEntity(
            id = "potato_early_blight",
            crop = "Potato",
            title = "Potato Early Blight",
            scientificName = "Alternaria solani",
            category = "Fungal Disease",
            description = "A destructive fungal foliar disease of potato and solanaceous crops occurring under alternating wet and dry conditions in subtropical winters.",
            symptoms = listOf(
                "Small, dark brown to black spots on older lower leaves first",
                "Distinct concentric rings creating a 'target board' or bullseye pattern",
                "Yellow chlorotic halo surrounding lesions",
                "Premature defoliation leaving stems bare and reducing tuber sizing"
            ),
            lookAlikes = listOf(
                "Late blight - lacks concentric rings, develops white fuzzy mold underneath",
                "Magnesium deficiency - interveinal chlorosis without concentric necrosis"
            ),
            management = listOf(
                "Prune and safely destroy lower infected leaves to prevent spore splash",
                "Avoid overhead sprinkler irrigation; irrigate in furrows early in the day",
                "Apply approved preventive copper or contact fungicide according to local label guidelines"
            ),
            prevention = listOf(
                "Use certified disease-free seed tubers",
                "Practice a minimum 2-3 year crop rotation away from solanaceous plants",
                "Ensure balanced soil nutrition to maintain strong plant vigor"
            ),
            sources = "Bangladesh Agricultural Research Institute (BARI) Tuber Crops Wing",
            reviewedAt = "Nov 2024",
            contentVersion = 1
        ),
        GuideArticleEntity(
            id = "tomato_leaf_mold",
            crop = "Tomato",
            title = "Tomato Leaf Mold",
            scientificName = "Passalora fulva",
            category = "Fungal Disease",
            description = "A foliar disease common in humid greenhouses, polytunnels, and dense field plantings where air circulation is restricted.",
            symptoms = listOf(
                "Pale green to yellow spots with indistinct margins on upper leaf surfaces",
                "Olive-green to grayish velvety fungal mold directly beneath the spots on lower surface",
                "Infected leaves curl, wither, and drop prematurely",
                "Blossoms and developing fruits may fail to set"
            ),
            lookAlikes = listOf(
                "Powdery mildew - produces white powdery growth on upper surface",
                "Early blight - produces dark brown spots with concentric rings"
            ),
            management = listOf(
                "Prune lower suckers and aging foliage to improve horizontal airflow",
                "Keep leaves dry by watering only at the base with drip or furrow",
                "Remove and compost severely infected plant debris far from planting beds"
            ),
            prevention = listOf(
                "Choose resistant tomato hybrids with Cf resistance genes",
                "Stake and trellis tomato vines off the ground",
                "Space rows at least 60 cm apart"
            ),
            sources = "Horticulture Research Centre (HRC), BARI",
            reviewedAt = "Oct 2024",
            contentVersion = 1
        ),
        GuideArticleEntity(
            id = "eggplant_borer",
            crop = "Eggplant",
            title = "Shoot and Fruit Borer",
            scientificName = "Leucinodes orbonalis",
            category = "Insect Pest",
            description = "The most damaging insect pest of brinjal/eggplant in South Asia. Larvae bore into tender shoots causing wilting, and later into developing fruits causing severe decay.",
            symptoms = listOf(
                "Drooping, wilted shoot tips in vegetative stage",
                "Circular entry/exit holes on fruits plugged with brownish larval frass",
                "Tunnels and rotting inside fruit flesh making produce unmarketable",
                "Stunted plant growth and reduced branching"
            ),
            lookAlikes = listOf(
                "Bacterial wilt (Ralstonia) - entire plant collapses suddenly without stem bore holes"
            ),
            management = listOf(
                "Mechanically clip off and destroy wilted shoots together with internal larvae twice weekly",
                "Install pheromone traps (Lucin-lure) at 10-12 traps per bigha (33 decimals)",
                "Pick and destroy damaged fruits regularly before larvae drop to pupate in soil"
            ),
            prevention = listOf(
                "Consider Bt Brinjal cultivars developed by BARI for natural resistance",
                "Intercrop with coriander or fennel to attract beneficial parasitic wasps",
                "Avoid continuous eggplant planting on the same plot"
            ),
            sources = "Entomology Division, BARI",
            reviewedAt = "Aug 2024",
            contentVersion = 1
        ),
        GuideArticleEntity(
            id = "chili_anthracnose",
            crop = "Chili",
            title = "Chili Anthracnose (Dieback)",
            scientificName = "Colletotrichum capsici",
            category = "Fungal Disease",
            description = "A devastating seed- and airborne disease of red and green peppers causing fruit rot and twig dieback during rainy or humid spells.",
            symptoms = listOf(
                "Circular or oval sunken necrotic lesions on mature green and red fruits",
                "Concentric rings of tiny black or salmon-colored fungal spore masses on lesions",
                "Fruits dry up, turn straw-colored, and drop prematurely",
                "Dieback of twig tips advancing downwards"
            ),
            lookAlikes = listOf(
                "Sunscald - papery white blister on fruit side facing direct intense sun",
                "Blossom end rot - calcium deficiency at the bottom tip"
            ),
            management = listOf(
                "Collect and burn infected fruits and dieback twigs promptly",
                "Harvest ripe chilies frequently without letting overripe pods stay on plants",
                "Spray copper oxychloride or bio-fungicide in recommended concentrations"
            ),
            prevention = listOf(
                "Source certified disease-free chili seed; treat with warm water or Trichoderma",
                "Avoid planting near solanaceous weeds or shade",
                "Provide raised planting beds to avoid waterlogging"
            ),
            sources = "Bangladesh Agricultural University (BAU) Extension",
            reviewedAt = "Oct 2024",
            contentVersion = 1
        )
    )
}
