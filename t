[33mcommit 8f77807cad6794f37114117b5c3897aeca2e86e3[m[33m ([m[1;36mHEAD -> [m[1;32mdatabase[m[33m, [m[1;31morigin/database[m[33m)[m
Author: Mateusz Chyliński <mateusz.chylinski97@gmail.com>
Date:   Sat Aug 20 17:33:50 2022 +0200

    Modified currency names model class, added new data class to have base currency in db. Added base currency to the db. It will have base value as euro (EUR). Added update and get fun for basic currency

[33mcommit eba2fb5ae17c702b5d1af1d57f1a6447f41bae9a[m[33m ([m[1;31morigin/fluctuation[m[33m, [m[1;32mfluctuation[m[33m)[m
Author: Mateusz Chyliński <mateusz.chylinski97@gmail.com>
Date:   Fri Aug 19 14:45:26 2022 +0200

    fixed code after failing resolving git conflict. The fluctuation layout is working fine now

[33mcommit 5e29118a12175d84481b1631c3d2e6fc308f5674[m[33m ([m[1;31morigin/main[m[33m, [m[1;31morigin/HEAD[m[33m, [m[1;32mmain[m[33m)[m
Merge: a5f76e0 3745831
Author: MateuszChylinski <30569704+MateuszChylinski@users.noreply.github.com>
Date:   Fri Aug 19 12:19:28 2022 +0200

    Merge pull request #8 from MateuszChylinski/fluctuation
    
    Fluctuation

[33mcommit 374583163dc71e49606bb42aa0336332703b37bd[m
Merge: 33ecdc4 a5f76e0
Author: MateuszChylinski <30569704+MateuszChylinski@users.noreply.github.com>
Date:   Fri Aug 19 12:19:22 2022 +0200

    Merge branch 'main' into fluctuation

[33mcommit 33ecdc45a6a2d74560e6c4ad7df7943546a2214d[m
Author: Mateusz Chyliński <mateusz.chylinski97@gmail.com>
Date:   Thu Aug 18 16:21:41 2022 +0200

    Improved UI in fluctuation fragment. Almost finishded fragment

[33mcommit a8aaf3e3c94f95b2651b3345813e81cdc3e5803e[m
Author: Mateusz Chyliński <mateusz.chylinski97@gmail.com>
Date:   Sun Aug 7 17:22:53 2022 +0200

    basic version of fluctuation fragment. From now on, user can pick from/to date. Just for now, it is possible to see fluctuation for all currencies. Custom made call will be added later on

[33mcommit a5f76e099d4e3a45d8333bb2a15f611e27b9b164[m
Merge: 5c54e7b d8858fc
Author: MateuszChylinski <30569704+MateuszChylinski@users.noreply.github.com>
Date:   Mon Jul 25 12:15:45 2022 +0200

    Merge pull request #7 from MateuszChylinski/fluctuation_ui
    
    basic ui template for fluctuation fragment

[33mcommit d8858fcd140f6fb582c2265cef97879248c728a0[m
Author: Mateusz Chyliński <mateusz.chylinski97@gmail.com>
Date:   Mon Jul 25 12:08:37 2022 +0200

    basic ui template for fluctuation fragment

[33mcommit 5c54e7b4d55d431a71bda18f82d833660781eb33[m
Merge: a6f35a5 b7c29e2
Author: MateuszChylinski <30569704+MateuszChylinski@users.noreply.github.com>
Date:   Sun Jul 24 13:56:11 2022 +0200

    Merge pull request #6 from MateuszChylinski/latest
    
    improved UI, this fragment will display latest rates for selected cur…

[33mcommit b7c29e29ea265028669e970d4b22b048832546f2[m
Author: Mateusz Chyliński <mateusz.chylinski97@gmail.com>
Date:   Sun Jul 24 13:54:25 2022 +0200

    improved UI, this fragment will display latest rates for selected currency

[33mcommit a6f35a5378f2c7bb0ebb05246b82137f2ab875a5[m
Merge: 1e8d8c9 1ee11a6
Author: MateuszChylinski <30569704+MateuszChylinski@users.noreply.github.com>
Date:   Sat Jul 23 14:27:31 2022 +0200

    Merge pull request #5 from MateuszChylinski/room_db
    
    improved naming in the project. Added database connection that gets c…

[33mcommit 1ee11a63e1ffd9950a03a190c2d77e319701d9a1[m
Author: Mateusz Chyliński <mateusz.chylinski97@gmail.com>
Date:   Sat Jul 23 14:27:06 2022 +0200

    improved naming in the project. Added database connection that gets currencies names, and save them into the room database, so I can use it to fill spinners views later on

[33mcommit 1e8d8c9748c0970e74e2252f28bbaffec2cc5063[m
Merge: 41dddea cb5e385
Author: MateuszChylinski <30569704+MateuszChylinski@users.noreply.github.com>
Date:   Wed Jul 20 13:43:36 2022 +0200

    Merge pull request #4 from MateuszChylinski/room_db
    
    Added room database with some test data in it. Will be deleted in nex…

[33mcommit cb5e385df4338c1a9d38f216631b319efe9e2e7f[m
Author: Mateusz Chyliński <mateusz.chylinski97@gmail.com>
Date:   Wed Jul 20 13:43:11 2022 +0200

    Added room database with some test data in it. Will be deleted in next commit.

[33mcommit 41dddea22c578425cf978e6988825cc6ca753c10[m
Merge: db5f859 4bfb964
Author: MateuszChylinski <30569704+MateuszChylinski@users.noreply.github.com>
Date:   Wed Jul 13 19:48:06 2022 +0200

    Merge pull request #3 from MateuszChylinski/conversionFragment
    
    Conversion fragment

[33mcommit 4bfb964648fb009c32ffaf35830861fab6c799b2[m
Author: Mateusz Chyliński <mateusz.chylinski97@gmail.com>
Date:   Wed Jul 13 19:47:21 2022 +0200

    basic mvvm connection for conversing the currencies, with hard coded values for now

[33mcommit b15bf8b8bfb3b0aeea2126a9e62a2c1826728077[m
Author: Mateusz Chyliński <mateusz.chylinski97@gmail.com>
Date:   Wed Jul 13 19:22:09 2022 +0200

    Basic looking conversion fragment layout. Partially finished mvvm connection

[33mcommit d8deba5a08072156ed14b0b75d6043c7e9b06ca8[m
Author: Mateusz Chyliński <mateusz.chylinski97@gmail.com>
Date:   Tue Jul 12 17:40:59 2022 +0200

    added fragments for displaying currency rates / displaying historical rates / displaying fluctuation

[33mcommit db5f859d7e840647840304f010a8c3d2cd6be6f2[m
Merge: 59aaafa 8384081
Author: MateuszChylinski <30569704+MateuszChylinski@users.noreply.github.com>
Date:   Tue Jul 12 16:57:52 2022 +0200

    Merge pull request #2 from MateuszChylinski/RecyclerView
    
    Added RecyclerView, and adapter. Created basic looking row for every …

[33mcommit 8384081ce570e17c3ba13ab013e418258e93a6e2[m
Author: Mateusz Chyliński <mateusz.chylinski97@gmail.com>
Date:   Tue Jul 12 16:57:00 2022 +0200

    Added RecyclerView, and adapter. Created basic looking row for every currency. Hidden the api key. Deleted currencies in Rates class. It is using HashMap now.

[33mcommit 59aaafaa852350380266235e56c09ac6de5cabc6[m
Merge: 0a1970c 73aca93
Author: MateuszChylinski <30569704+MateuszChylinski@users.noreply.github.com>
Date:   Mon Jul 11 16:14:44 2022 +0200

    Merge pull request #1 from MateuszChylinski/mvvm_template
    
    basic, working mvvm, and retrofit template.

[33mcommit 73aca931e32a211a4100766731c49f2ca403e25e[m
Author: Mateusz Chyliński <mateusz.chylinski97@gmail.com>
Date:   Mon Jul 11 16:13:48 2022 +0200

    basic, working mvvm, and retrofit template.

[33mcommit 0a1970cf334962e212d6be32c194cf8844465bfd[m
Author: Mateusz Chyliński <mateusz.chylinski97@gmail.com>
Date:   Sun Jul 10 14:28:36 2022 +0200

    init commit

[33mcommit 52bfc3b495a1e35e0d3b510919d0531bb9aab7b0[m
Author: MateuszChylinski <30569704+MateuszChylinski@users.noreply.github.com>
Date:   Sun Jul 10 13:45:47 2022 +0200

    Initial commit
