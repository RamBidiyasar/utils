package in.wynk.secret.manager.utils.kafka;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class KafkaTimeWindowRepublisherOptimized {

    public static void main(String[] args) throws Exception {
        // --- Configuration ---
        String bootstrapServers = "10.161.24.22:9092,10.161.24.24:9092,10.161.24.23:9092";
        String topic = "xstream-wcf-events";
        String username = "appuser";
        String password = "uJK67AUDI1Ax";
        String baseConsumerGroup = "republisher-group-"; // Base name for consumer groups
        int NUM_CONSUMERS = 1028; // The number of consumers to run in parallel

        Set<String> UIDS = new HashSet<>(Arrays.asList(
            "i8KLvk6PPZc7Ixrri0", "0bh9bxrcFvkJtkMuk0", "ILkjxVeL72E8c9O3C0", "GE4uU8xqqP7hrtFR20", "oQ6VaTzCz-apCrQyv0", "90_qDq9XLGLNmtvsX0", "zT2zZ87F4V8JcuS9B0", "_KOkcd3esB8fxf_Je0", "-yLhO-JXd0V3wjJxl0", "q-6Fuem0zNQ-c1mKl0",
            "kARL6oFrAxJaF50c_0", "L-SapRPcff5QqX7Ir0", "zFOnydCAw6n_V5eRh0", "zTbsi3hMuf2W9L0290", "OwjSKg7bAHAyTfCjy0", "iT79VSfufkHCEAXez0", "OryVwmCHyS13liDpA0", "QZ2mxBamOZnlQS7u90", "twdi-wTHsQIKReq250", "Z6MAXDwe8Sj4Kt7LZ0",
            "qOaOkAHaAWvivzMfs0", "vbhaWmOOrQyOD7iWj0", "PlM5fuWSrH11_jFYA0", "RmtXPa9aVuyLAB18V0", "5Z8ixjkD3pYYFugBy0", "OyYW8bx8ntEJOaWd90", "mqNBxgFaDAxFo4mbk0", "l9RmFKYZdceI3bD-E0", "WeMZJh7tS8ZmG8ABb0", "PWAQRH8fFCI5IiVGp0",
            "kiXr_sItWk-s55f_S0", "FKztKHvR00tUS12fz0", "hxjxQBI7tatcc2IQW0", "zdrS7_wkw0fQz95sL0", "vdST90iv-7d3KoN_s0", "gPebWUVg0Rs_FWN580", "kMNs-PhNJZCVpkV_20", "DYHBqiTGCqke3Lgg_0", "BHD8ZM8CmSgr8-KTh0", "-n5425l3kxMR-10PJ0",
            "LIo5IQNc_WIwMk2sn0", "kG8HnyjyEaoCJTD3u0", "zqf47PpqddIjCw0p00", "imWk5i_0gNgj9Lh740", "AwFaqsvjVeNIGvQyT0", "FRW3MwnMw-niVePz20", "UC7ibksJ0gJrh-MZT0", "p2LXzCM0EJqepLzH40", "uqS202E3NK5KBk-DA0", "Q6DnuxCybwxyRN8S_0",
            "yQ6anv6Tzl74XENzu0", "P2vTz0vFsowEUDUj10", "1TRNxkkxaCoxQc1ut0", "i-CfaOdUNBZYk2B5X0", "uYcENMKEgaelZYSd10", "1jSOvwBt23qPqVlJU0", "31p2gpTecncKjF7e20", "QRAzs_cmFhZRdFnek0", "40m4lK756kpKIrxFL0", "RU7tSb_wyfYNSjEwH0",
            "faNuIqokpGdQkw1760", "n5Yf77jSpbrEnpTcg0", "EZnOabKZK_EzwsmQj0", "EutFV_CNQlcTjET-q0", "kbD0nGUOXubBb7LbC0", "InbH6l6A_yVXMWzum0", "9cWej8k09QWlLCpCH0", "8DYx2KFlpIQ_kD5kz0", "JnTQj_xJ5q3-1WorU0", "-iX4kcDOnWiYii0MH0",
            "JAUnwEL43daKo-s8u0", "hROR9gf92CYWnpn9H0", "2DxoKtPosVuGEiAJN0", "6nnCZu74OItzFfaSL0", "GRSlA_Fbc1Xb9CgWW0", "9DtMbrHgNlxAZQiS10", "l1fEE-jHxnMgRRmcz0", "yENeQU_ZrnfaduqfU0", "X0QCTieM4eATmya_j0", "yiFSgh4ZkBx9MftXA0",
            "883LxSXSC6nthJiKG0", "F_0Dwin9Li--ehjzG0", "8v309zBus3oHanGB20", "xVGacBwnqK6b6EmOU0", "4MaZtgOjKjQdF3Ch90", "qWAhr--PpBAtgli0t0", "zfMkFtVI_HINYvfMP0", "1lTXASOf0L5geeIig0", "rdWTR9aBcjYvU0X_t0", "MwWR35KRvcUR4KX8j0",
            "0z1iUi_w_AFQxdXSl0", "_kR0_m2jzmnkoavWX0", "_LaKpZ4AuKlcX3sCl0", "L9yPjS3YcGFGqCVsr0", "iKMEgzweT8E2LZcKM0", "7DRw5lnC8WbtM_-br0", "xK6b8MdSNBUpO68Dz0", "h5pTv4T1wYm3bjJ6i0", "jnK9Uq4144LMbQTcU0", "lfL_nve34V4locb3T0",
            "5EORSZ9fuLQLqXM4K0", "k8ZCgMFzSrwt22mYd0", "7Mrkc7-B7Vs0rQhef0", "nBOowDNkJhBFhk9Xf0", "lSBRWuar8utkC0NNL0", "MtRuL0iaC1HHHKlaV0", "bCUBZvCc1iDzi_UPP0", "s1eOq1ITx0iEERnJC0", "fm-tJFllRsWwrFD3Y0", "IURR16QVJdNfPbqne0",
            "Bx_JloqAnE_g8nABT0", "cQ3aHaeKO3cjQBII40", "EYYQprUg87WLP1kSG0", "vBhajB7wD9zFb5U8J0", "H5TBGzFC57FMykoaR0", "m66PKUctjmzuE1a7k0", "kjAJnZbYNp_cgPwjb0", "vZgURTsCJ9sT9FYQx0", "ItMekjRUrSbHQSDL00", "A0jWW6kIzDt8WIS5l0",
            "hzPKsPXEi89ERriHM0", "FJaGgs5JQEl8oy6Ze0", "xRbe1j5H1p7G869kq0", "XAamE4Aocn_iM6wFy0", "VjDd2rF-Bwa5PO-rN0", "aWBWU2JYulPfQzm420", "mi2tp13RGjFJ-DFVN0", "cWfBLKheLOPUhm8Ru0", "zTQP0iTwKkhD0OQZu0", "xMgZHlXjdRP1Fo8iW0",
            "f9Hq_lNgRJS56dKpV0", "2Q1Pb99oNwLFP9R520", "fS4ukcIvJrUGYv0lY0", "ndUxFGySKVuCiQqiF0", "3mPDbAey7uc5EXd-A0", "oMKkFS-mhQYUSB6Lj0", "QKFBkc7ED_OM6jZkT0", "u-jnEmNxTD6q_XY-20", "tUyp78Ux07ImZszLM0", "i_6Yxq9arbWeIV1000",
            "whwDHFP9kbrG3YU-f0", "1zabtfYaoN0x7hLcx0", "_FL0kVEbJGPrSuvjz0", "yLD_hfhbCMw1geg1M0", "iUZcU3nuXnQbBQNW30", "HCZAe22rlKSPoFOfO0", "6vLQU1gEORMSwOBbE0", "PGXDcyODeOqb3h7ec0", "N6wT_rby69JWRpmoK0", "F1Z76uUJM22ZJ8Hdd0",
            "6UOveSl4Mr-YLn_lA0", "rGqIQC_PIFKL4QFkE0", "QNCAOvNzjfp6Lb9sV0", "_AceN-QWwyzl9M09E0", "qo2nEE8-PMD6JiALo0", "7I3X8iouDUKHxYBm40", "F5E9aLlRL-UWgrsJw0", "CzP35LZVOYKM60Vg30", "AI1kSQAe_960B2FCq0", "T3x5OLQAQ3-7eefLr0",
            "_3Xjka2UZ586wpmkQ0", "TvLkoomoyt0zNstxA0", "n07zM5fUo_RrG34Ts0", "wkz3cNv6z6qXtE1AL0", "J_qt8k3ET2p_nz-s40", "Hz3Bppm54IE1bF2sM0", "BaCq7Uwxp_tN6RUgY0", "Pd5L0k9I0uoOSKl860", "ocVT_uLCckeVzRRDr0", "TfZdUYk_vxkgtyq8p0",
            "sThOtzE1rKyEgjybR0", "YK3yLArMHRDeQP0ri0", "Ap4Fu-rYBnVKzDEBE0", "NXK4BpC0QMha3eS4e0", "yJZyhCL5q5dG0pequ0", "-Zzx3xtiSeyg2s0cL0", "COwgh749UQ4QHMJDJ0", "9uKqVG3iQiS--JiKd0", "9W2Dl8S70p3l4SSyf0", "HvfMKJcFAMQkISkHp0",
            "vgXv4CtVHBoTfUnMo0", "2fVEf3W0sBPnYK_VA0", "F9dj99iWS1q27-3Xs0", "XsvViCY38PcMHBiqp0", "UATJ1PcYNTOP1dER10", "4WnzIpH0ik6BtcfW60", "BhqTX98TvalhnyM1C0", "70I9f2QF1yl5pTJCJ0", "id8W0R3Cq8GRqXDcN0", "vNYapKZit4PBXBTlU0",
            "tWDrnA_Mtw4zpwWnj0", "KTmdicIIF_cCNX4Vj0", "kalqgBZ6kRjipHOwe0", "a6ePix5_X9bs8YSQH0", "KwfXEe0iVzyMJjA8P0", "U129YmbEHxmG4J4we0", "9_3WQxq6m9orQ3uXn0", "HpZdEmv5nDDKrx2On0", "aHJTglNh3gt5slRN00", "x9WU4pARnodGgUIuc0",
            "6YnZa41QTEbZyWjue0", "5-PegWltetgN-AvQG0", "k6x2xaDWzQ9L5NXm00", "ioK2G-FOQEuFEA7N60", "diwaHhMVKee4YkrHZ0", "mgEUN9ct910KV56Zf0", "s8T8XqstyYfKFJI_N0", "2bDkcVJG0XCQabEr30", "y7IceIGoF-afKrT6Q0", "X_2w2rt5fRpDRPAnZ0",
            "-LC5tyftAwJmXH4La0", "zK_HRj51anp--GWia0", "H48bSShxHVPvPPxpX0", "ThIos7iK7rHHVw-hS0", "equV4S4Oaq5jsV8S40", "5KBCYvu_nlslAGUlE0", "q0EaFNTJcrrLZ-1ho0", "pu0AOivKfPkg30gPL0", "Xi5iuG_lcJreBVnNg0", "GVan4_v8LxuF7evT20",
            "dn6J71mTKPZV5JVIr0", "vz2HFKSacviWWP77D0", "yjNuS1vDAU9EOXGQc0", "Ewz-h8Qwt8ZBfc0Bc0", "N25njGwVPhzREuNSQ0", "f9BUqMhWou759MHrQ0", "jsBN83WkZSRIiGzAr0", "QTEkYDIFLCbAMUm3e0", "gAvag8MnZ4lr-K4RK0", "9YWy4DJ41H1No3ce70",
            "-C4b7jGP60caKxPNG0", "f3OhcWkmTpAdtzp3F0", "fxoFL-ww68VqfBLhK0", "SGcgbo3qWLLhew5Pk0", "mGO-Dt9oXZfMEz_0F0", "9lIQEZD0UU_Ai3bhF0", "NicFAvf7L_3qHm3KP0", "Zeq8kyi7_h0PPBtKc0", "nFvGfjRDpM1a6MSlx0", "ZkvOCnzi2IpgDgnGq0",
            "5fj8FOPc5RjyCEaKX0", "_NpJHOkrY9M8LaKbz0", "awM-bH-IxhNIlB_Ds0", "SK2Yrp3jekXIbkcd80", "limAYQqKa5KeFGAhG0", "Q07C4hOQTkR0wwKnr0", "AAqxdOHDSFFeqz6c10", "jBEiy8E06adv3cCWM0", "EAHP-gmbKODQItiDh0", "FG-sbEdAxO-SwXyRz0",
            "bH8cpUstMg-yRN4Z80", "5ungrk8LNhzItwQay0", "m0136vD01Df1VgekH0", "eEkzqbAamDYkevHLK0", "Ht-cm1rZdaJpjrqx30", "CRMm6Vtq8BluCl3W70", "mFEjwR0uEVK-P32Df0", "PfJG8RzkfZk15kGe_0", "XxSOyH-Gjmfj7p2_k0", "A30EG4E2zCpN0n8PO0",
            "8g_fOABgn3UIWMT060", "SD4nnsqxUcRJT6uQN0", "nOtwatceClHilVrGH0", "BLegichCMYaUCUaMj0", "LZ2YquYJNpvCyHrZ20", "Lg9PyOfer2E0577TA0", "NGGvUtW7qoDAlbwAL0", "rdB1_E3IJGVw2d3UQ0", "oV-Z-doL3_W1tqCK-0", "H522skcHE9Z_I0hiS0",
            "OfnF1RyAMPnd4ogcd0", "AKVl0LhMpvmKmnzFK0", "tn4G_NXatnjlyJhS30", "3Q_NuQRbhNcJasU7l0", "f_osWcsBom805f3fj0", "H-CuDYiN73nfGL89K0", "i1TH9MxyLZHJEw-Qw0", "PUXKkKs6YwZSi59rh0", "CJQZ8Oba-hjPAz2Uy0", "k3EBaopZEpNXApKQs0",
            "uW7oH95ElmIclW2rY0", "BZwSzOzgprvzM3GU20", "qbHgdVqMjaFYh_VER0", "CKGhIE_yP-Uu9yign0", "DFvbbfB-28IsRGXdz0", "e87jyWLh3NooqetLu0", "BYae5eFFJvHYiPghi0", "AVxCq8boYFjOnMZs70", "t30uv1PBqS27IUVQF0", "Wnfe5i6pT39c5Rg_A0",
            "cRnWd3Wapq1GwGq3i0", "oEoTBoqS91oH5nti20", "U6KyisgopDMdVh1J90", "ncdsIIwsExfT7Bbag0", "XLGsAn--qgaOL4Qgh0", "Sb_znCgd5bGVPekRR0", "ObxvNpDc7XUHdDLIF0", "Nk7DHMZokUs9he7cc0", "dRxS6yJbPdIHcfsHZ0", "VYbT2wPJ1-t4iklEg0",
            "KuY4VMqkpduPdbuR-0", "mrP8VIO9WhPN8_sRK0", "3wQmYbJnNwnI-lt6E0", "rRICsGpCqrZx6wmgM0", "wXNWI4GY2xeOYc__70", "xKrIz8M1LEEFbBF-t0", "ePMas7qeb_v1FzNnu0", "oJ5R5sMidkqH6KMVH0", "iCzzne4heCEPCz-aA0", "xdN6fAaiVW679HCLv0",
            "wRD1h27WS3ELaDeco0", "ymjR2Q3eOVsBCTTp90", "IPYull-8bgx7Nal-u0", "Bdv5RZ0-Rq3AYDSSD0", "Qxu5NHZACkrazGD6y0", "jmYd_Nj-uL_9_SuWf0", "CY8Y2L_sr5UEGVesl0", "1w6LOXuxw3rizNP1Z0", "NSm3B435DE-qgB53U0", "pXjw5U0I5KQiQcPSb0",
            "4Y22offPGvxuM-SYG0", "4-e7bXEYUmsPSDGUp0", "prpKZlFikcuxbgnfM0", "H0x8gtA52SWBenEmJ0", "vndyCwHCEMqex_8ro0", "QATUykhcx2vKZBohn0", "pW9UO9sOGI9glGhQ00", "0xZCJ9mZDBFaKIRNv0", "vctJ9lmXwjGlnN-t10", "ZAnZ6w11tXcHhDmOx0",
            "8kaeOVCEi-G6-_YT20", "6DotwH-gfiEHdCwk70", "QJ_30WUarWHW5dkeC0", "L7jCpjbcBDiYB_2ll0", "5eI-KCg2-3y7y3ZJY0", "T7KRBpg-C3RCm15xO0", "S9qeVXSqUm9SSkWNc0", "T3CBa0Xd5tKQGc5RY0", "oXg1v0KRG0VWNkIsf0", "OrSzu-hHUDkJjI8fR0",
            "bNebV772xh8OU4CIB0", "ApPHXYHK7FKNQ674q0", "pEd2H70WmGuR8U8PX0", "rANuJzF1F_e_OKF_G0", "53hEg0llCk3YSbcI-0", "j6MysQF3-_TVYRc5b0", "aKK4qngYxXprEi6Hz0", "GyNXCZUv6u18eIG_c0", "C60uIbUwxCgXwfV870", "2aq6zMBWHlNKNRCP80",
            "nqQJ3LllQd1eRK5tK0", "1EnMoEfazOFdGAt-c0", "dgOViAfG-Vt49hUYa0", "VazpbnOSHvT4McwC60", "Ju1T87wscqTrSznnu0", "1KsxKEziZ9TRL9zu20", "DrEQbqQHzMWdI1OT80", "rlLlC0bGKhU57sw2a0", "LxviypbLKxhz3MDRl0", "34CeUIO7SMNp8BpTt0",
            "14lOClEXSv4-sRSOO0", "WY9RoHBthx73bkPJy0", "eivN1487SmVcgzXX-0", "6_gVrQhiEe_dXUq520", "jMBTAfHseaMoUP4SJ0", "-ysmF--s6rC7JOvez0", "UKKLB_1x07jTTT9BM0", "BKH3p19u24ZPH6_Bg0", "Sx8_7F2NzXWNa5-0Y0", "he10jxBcCH4HG88RC0",
            "zd972cJMlJoVPyH4P0", "7JUfGMxhURUDY7NKe0", "doAKx-Lx2zw42oN8_0", "1865PHOu0WrLzDizP0", "2tDR2gmWnSzXlYE6_0", "maie48_4QUlH9mdVw0", "punTHmllGJ7gaSeki0", "tMb03_9u_I93u8eU40", "xpgeC2G-d0LRrrM4v0", "axl8yqX6nHzITnpNG0",
            "-FLyQdiKxNSCJuP2o0", "BbZW2oIPa96UO5zXL0", "wn5sgDSxpfRzi0ARH0", "yHyERBjMSFNvv2s8G0", "ZppMJYAUxtlZ_Xh_X0", "IoVecOtbriECTaJcT0", "srruCGZj9W9oykYxB0", "qzYzuJAujqH7Yxs4n0", "i9Va2Z3rG4u5KDEby0", "qI-jSY5oTC72GPY9_0",
            "GXWRceZNEYYUqfJ0A0", "Yi1w2miz9E7Ab0dd00", "PQmDUanJLzogNYV7s0", "FhxjbSqyldKEdWvvN0", "xJpzSeHDCw2yKEVEU0", "Yblimp-BctpoBo1tE0", "AFTtORvDQ9EoyuMm-0", "k30sJGstC_E3RmMbW0", "afZqRIK2DyCPJnuLn0", "-ymVxAAKKLuRXS4NX0",
            "ifPg5kkkzwcCn62xV0", "limikEuiOd9RwtSds0", "uOaM9BBS1SPj5lorl0", "QiCV9Qr_aIgvV7Kn60", "YxrJUVHnBsbyVBsxr0", "3gcwK_BNpCf1yUrs60", "OHuS0gE91fec4bjHA0", "obyQXppTon4svjEYV0", "Zwr-76olPRIJ08Vsh0", "VgEMSZ-Z4RrYtrFAH0",
            "jSqlPpELlassUbLrP0", "1f10QEiYAZtn80VoN0", "w4IuUQ-3eDgfctrOq0", "HEZG0zYv62KingFCf0", "RZaTj0JylmW8ezFj30", "kDxp7h2N-7e7VQ5Qw0", "J_Y65wjB5xRMk85ai0", "bea7wjcZzv4RC9iG-0", "7JFGvNAlaWPNXxIuY0", "595SFzjjBb_KMKyf20",
            "ooJzMfdRwCJYYw36o0", "MZlbcHbboT5yDVMw00", "1pu-yzXFO7zu7x_Pp0", "msl-WtyyM9qgBSGNU0", "xA6MrTxSOb1rYzkOu0", "0L7Kt9Qwepsu4NAQu0", "V1E61fc_Y4QUyP8kr0", "mCQ6r0EIkuDNj9DlG0", "XUPvgfMSfjFNROzwa0", "QvKO4rv8jUiXpRy310",
            "WbYs1NFgRZrqv_V1J0", "05VWWVepu_5tR-HVP0", "3rb6eRHJ6uPiuBf1b0", "ZBCvo9N4fiP2a1DOP0", "_JmBtOVPJ3DG18LXT0", "4lAkylYKD_x2j6APw0", "dP2Vbx2EgoYDM5H-P0", "Wxiir3rNsxtlOcfpe0", "WJ3qzAVf7l52NmLxm0", "xcNqU9_qdUVSsP-7X0",
            "1MKlgu4N_7X1Sbe0F0", "EhdIuo8hNT74aotLI0", "r0hlU2KQFQiNXVqHK0", "VH3Kn7rQlPJdDrH-X0", "OZN6oO4hLcDo3OX5o0", "6mncS-NBB6sEWXgqp0", "PkQTWX4geYHNdS6ye0", "dkwJb_AQLAi-NEI1R0", "h_vPgKsNTR_x6hg4N0", "ZQabfXVobTyQsshPu0",
            "HtMTx_92HE19jviWY0", "CIPxThwZN4Qmt5RPS0", "464D0Ue9f93oBN9590", "RbTVAvEDOHHlU_-0T0", "ta5GiO_drMOW2EtI70", "UWkPoESzlcbLR4V1Y0", "ZyLZaD0DGdMn29scc0", "c04PoXqjn8w3eCM-80", "OCtLs_Y1uz_EJKJIK0", "DwCIQ7zHlOW_7bV0P0",
            "lfBBICcA792l8QW9U0", "QeY2GUt0I94pfnlRi0", "g0fmwIHkOjSYafO7l0", "fwBjcbe_4akxfcJBy0", "xL8HYnTHdZ1hCiWsO0", "-uM-E0GugVy8ki50v0", "JipjHtlxsN5CPxeuP0", "Teo7ulBdPHpDk6-vP0", "lelpFg4V6JIEQt4R60", "xHiEHdK1_BDRd4n9Y0",
            "q3f5svARWFZJ9PzKe0", "zhyh-o1r-GAr8jKfR0", "cFmp9XNJiCUm8JRHZ0", "ztOEFMGq9F0QXJ1Ek0", "AGnk1i80A-ZkE7rYj0", "YbVoaH6ko-dxgEkMb0", "Yg35iZfCXGi8QHHtJ0", "nTO11S97tLJ53n1qM0", "Xhx0WolPHXO04o8Rq0", "9oY_vqrXoZ916IGfH0",
            "w9AD-d4MHK5yFeK2Y0", "AzSmWIr5QQB28wOOt0", "7Pk7ZQ0Nw3krXKqnl0", "E0N-iZpfVlxLQjpv10", "FY2iqfVAzwxkR19AO0", "6UeUP_UrlEbf-Ddn90", "vJu89-dUOQ1v4AShd0", "VUOyHtylHF21FSi350", "ZGxJQd46zvOTyxyXl0", "SGBnZQkhqmpo9cgCk0",
            "_N8bfodjiPbwkLeIH0", "LfEMinMOP83i_jjbP0", "E6gMdi26GKvfV2Wvd0", "cQtOLa6ju-GEknc1A0", "HB7XEvdhFoBuCircv0", "hwu3yMVqpoEuGUajz0", "u40dRtWUnue3bubHM0", "Jd6YQhSWOMAjZrM0k0", "DIpYVt4a2bVkU6iEn0", "NkmTgH4GrEpRCwrcz0",
            "sJvr3df0qefgG-vE20", "wgxDKnmBenbi8Qo9-0", "JhManBGQ9cDuFdlox0", "qo8QSCY1nBQpbplkP0", "sjL9b2dtmQ34rOC6S0", "Y5LVF41DSsjsIaa1s0", "bwotEB1NjAEFd5gKE0", "pZB6y9nqARHleVaWQ0", "AVsCsYOxdzCXkGzDh0", "SXYBJrtk0CVlSn6gc0",
            "bRA5cUSxSS1WWRo6A0", "ZsdKKMuHBcjlm9JKs0", "yKYPaFD3Erien9d240", "Vyu6NXSLcGScF-cfO0", "4TAHlaCHc1ruJmncA0", "2Xx9mfSXLCBM98V170", "WnDZ8UYm83HxL2-i70", "YVEaYEgGauqF7kY8v0", "3W_hZ8HJgsJCAZXlH0", "6ex-5oFjbaLcbfp1Z0",
            "leadz6PPke-Wtu1LP0", "_RPem5LKWLRoL6a420", "izKyQgIbJjeeKgNuY0", "IPGJl3EHQdYzvQLlb0", "zeSeBQyieB-K7rZFn0", "9CZvcvBKtq5ioe7Xv0", "JMsd1iBbtOK7wEoaa0", "TJufBwx9WWclVag2q0", "MbfZziBb9ZmQ0nQHK0", "MUUE5ZKV47p8DBzb90",
            "K0WfJtgKRf3ofagvn0", "uzpN8l6jDZ3gUHsNr0", "_sfwrocu-pNAUF39c0", "QMXiwMs-7_adBRZng0", "0C0USQmmiEmJyhlMb0", "UoyrAl7KrXX0mdiCC0", "OeVoI-jEHq6dHjkns0", "rMc1Zmnfq5vqjrajT0", "SI0wawluGFnvllmki0", "jMmxLsczEabOSGiXb0",
            "lzAF5EbK0ZbMnvu8H0", "crDH2Zmb_RjZS_Ly40", "90evm1Y-VD710vbPU0", "bUkk9VddRrJ_9wXRj0", "0n7WnBOIXeK8T1pcL0", "BtlGht-OzllnWaibH0", "f-nraXKLTqe1xeSgL0", "POFASELy9AHztt1j20", "P8vKi73waxyKgClJf0", "s-CRh2KWo-lZFb-BA0"
        ));

        // --- Time window we want to extract from the log (yesterday 14:00 - 14:30) ---
        LocalDate yesterday = LocalDate.now().minusDays(1); // Ensure this is the correct date
        ZonedDateTime startTime = yesterday.atTime(14, 0).atZone(ZoneId.systemDefault());
        ZonedDateTime endTime   = yesterday.atTime(14, 30).atZone(ZoneId.systemDefault());

        long startTimestamp = startTime.toInstant().toEpochMilli();
        long endTimestamp   = endTime.toInstant().toEpochMilli();

        System.out.printf("Target time window: %s -> %s (ms: %d -> %d)%n",
                startTime, endTime, startTimestamp, endTimestamp);
        System.out.printf("Starting job with %d parallel consumers.%n", NUM_CONSUMERS);

        // --- Common SASL config ---
        Properties common = new Properties();
        common.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        common.put("security.protocol", "SASL_PLAINTEXT");
        common.put("sasl.mechanism", "PLAIN");
        common.put("sasl.jaas.config",
                "org.apache.kafka.common.security.plain.PlainLoginModule required " +
                        "username=\"" + username + "\" password=\"" + password + "\";");

        // --- Consumer properties template ---
        Properties consumerProps = new Properties();
        consumerProps.putAll(common);
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1000);

        // --- Producer properties ---
        Properties producerProps = new Properties();
        producerProps.putAll(common);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProps.put(ProducerConfig.LINGER_MS_CONFIG, 20);
        producerProps.put(ProducerConfig.BATCH_SIZE_CONFIG, 65536); // 64KB
        producerProps.put(ProducerConfig.ACKS_CONFIG, "1");

        try (AdminClient admin = AdminClient.create(common);
             KafkaProducer<String, String> producer = new KafkaProducer<>(producerProps)) {

            System.out.println("Step 1: Calculating start and end offsets for all partitions...");
            TopicDescription td = admin.describeTopics(Collections.singletonList(topic)).all().get().get(topic);
            List<TopicPartition> allPartitions = td.partitions().stream()
                    .map(p -> new TopicPartition(topic, p.partition()))
                    .toList();

            Map<TopicPartition, OffsetSpec> startReq = allPartitions.stream()
                    .collect(Collectors.toMap(tp -> tp, tp -> OffsetSpec.forTimestamp(startTimestamp)));
            Map<TopicPartition, OffsetSpec> endReq = allPartitions.stream()
                    .collect(Collectors.toMap(tp -> tp, tp -> OffsetSpec.forTimestamp(endTimestamp)));

            Map<TopicPartition, ListOffsetsResult.ListOffsetsResultInfo> startInfos = admin.listOffsets(startReq).all().get();
            Map<TopicPartition, ListOffsetsResult.ListOffsetsResultInfo> endInfos = admin.listOffsets(endReq).all().get();

            Map<TopicPartition, Long> startOffsets = new HashMap<>();
            Map<TopicPartition, Long> endOffsetsExclusive = new HashMap<>();

            for (TopicPartition tp : allPartitions) {
                long startOffset = startInfos.get(tp) != null && startInfos.get(tp).offset() >= 0 ?
                        startInfos.get(tp).offset() : -1;
                long endExclusive = endInfos.get(tp) != null && endInfos.get(tp).offset() >= 0 ?
                        endInfos.get(tp).offset() : -1;

                startOffsets.put(tp, startOffset);
                endOffsetsExclusive.put(tp, endExclusive);
            }

            List<TopicPartition> partitionsToProcess = new ArrayList<>();
            long totalMessagesToProcess = 0;
            for (TopicPartition tp : allPartitions) {
                long start = startOffsets.get(tp);
                long end = endOffsetsExclusive.get(tp);
                if (start != -1 && end != -1 && start < end) {
                    partitionsToProcess.add(tp);
                    totalMessagesToProcess += (end - start); // Calculate total messages
                }
            }

            if (partitionsToProcess.isEmpty()) {
                System.out.println("No partitions have messages in the requested time window. Exiting.");
                return;
            }
            
            System.out.printf("Step 2: Found %d partitions with data. Total messages to process: %,d%n",
                    partitionsToProcess.size(), totalMessagesToProcess);

            List<List<TopicPartition>> workerAssignments = new ArrayList<>(NUM_CONSUMERS);
            for (int i = 0; i < NUM_CONSUMERS; i++) {
                workerAssignments.add(new ArrayList<>());
            }
            for (int i = 0; i < partitionsToProcess.size(); i++) {
                workerAssignments.get(i % NUM_CONSUMERS).add(partitionsToProcess.get(i));
            }

            ExecutorService executor = Executors.newFixedThreadPool(NUM_CONSUMERS);
            AtomicLong totalConsumed = new AtomicLong(0);
            AtomicLong totalRepublished = new AtomicLong(0);
            CountDownLatch latch = new CountDownLatch(NUM_CONSUMERS);

            System.out.println("Step 3: Starting consumer worker threads...");
            long jobStartTime = System.currentTimeMillis();

            for (int i = 0; i < NUM_CONSUMERS; i++) {
                List<TopicPartition> assignment = workerAssignments.get(i);
                if (!assignment.isEmpty()) {
                    String consumerGroup = baseConsumerGroup + UUID.randomUUID();
                    Runnable worker = new RepublishWorker(
                            consumerGroup, assignment, consumerProps, producer,
                            startOffsets, endOffsetsExclusive, endTimestamp, UIDS,
                            totalConsumed, totalRepublished, latch);
                    executor.submit(worker);
                } else {
                    latch.countDown();
                }
            }

            // --- ✨ NEW: Progress Logging Loop ✨ ---
            System.out.println("Step 4: Monitoring progress...");
            while (!latch.await(10, TimeUnit.SECONDS)) { // Check status every 10 seconds
                long currentConsumed = totalConsumed.get();
                double elapsedSeconds = (System.currentTimeMillis() - jobStartTime) / 1000.0;

                if (elapsedSeconds < 1 || totalMessagesToProcess == 0) continue;

                double percentage = (double) currentConsumed / totalMessagesToProcess * 100.0;
                double rate = currentConsumed / elapsedSeconds; // messages/sec

                long remainingMessages = totalMessagesToProcess - currentConsumed;
                String etrFormatted = "N/A";
                if (rate > 0) {
                    long etrSeconds = (long) (remainingMessages / rate);
                    long minutes = etrSeconds / 60;
                    long seconds = etrSeconds % 60;
                    etrFormatted = String.format("%d min, %d sec", minutes, seconds);
                }

                System.out.printf(
                    "[PROGRESS] Consumed: %,d / %,d (%.2f%%) | Rate: %,.0f msgs/s | ETR: %s%n",
                    currentConsumed,
                    totalMessagesToProcess,
                    percentage,
                    rate,
                    etrFormatted
                );
            }

            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.MINUTES);

            System.out.println("======================================================");
            System.out.printf("✅ All workers finished. Total records consumed: %,d. Total messages republished: %,d.%n",
                    totalConsumed.get(), totalRepublished.get());
            System.out.println("======================================================");
        }
    }

    // The RepublishWorker class remains the same as the previous version
    static class RepublishWorker implements Runnable {
        private final String consumerGroup;
        private final List<TopicPartition> assignedPartitions;
        private final Properties consumerProps;
        private final KafkaProducer<String, String> producer;
        private final Map<TopicPartition, Long> startOffsets;
        private final Map<TopicPartition, Long> endOffsetsExclusive;
        private final long endTimestamp;
        private final Set<String> uidsToMatch;
        private final AtomicLong consumedCounter;
        private final AtomicLong republishedCounter;
        private final CountDownLatch latch;

        public RepublishWorker(String consumerGroup, List<TopicPartition> assignedPartitions, Properties consumerProps,
                               KafkaProducer<String, String> producer, Map<TopicPartition, Long> startOffsets,
                               Map<TopicPartition, Long> endOffsetsExclusive, long endTimestamp, Set<String> uidsToMatch,
                               AtomicLong consumedCounter, AtomicLong republishedCounter, CountDownLatch latch) {
            this.consumerGroup = consumerGroup;
            this.assignedPartitions = assignedPartitions;
            this.consumerProps = new Properties();
            this.consumerProps.putAll(consumerProps);
            this.consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, this.consumerGroup);
            this.producer = producer;
            this.startOffsets = startOffsets;
            this.endOffsetsExclusive = endOffsetsExclusive;
            this.endTimestamp = endTimestamp;
            this.uidsToMatch = uidsToMatch;
            this.consumedCounter = consumedCounter;
            this.republishedCounter = republishedCounter;
            this.latch = latch;
        }

        @Override
        public void run() {
            try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps)) {
                consumer.assign(assignedPartitions);
                for (TopicPartition tp : assignedPartitions) {
                    consumer.seek(tp, startOffsets.get(tp));
                }

                Set<TopicPartition> remainingPartitions = new HashSet<>(assignedPartitions);
                
                while (!remainingPartitions.isEmpty()) {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));

                    for (ConsumerRecord<String, String> r : records) {
                        TopicPartition tp = new TopicPartition(r.topic(), r.partition());
                        if (!remainingPartitions.contains(tp)) continue;
                        
                        long endExclusive = endOffsetsExclusive.get(tp);

                        if (r.offset() >= endExclusive || r.timestamp() > endTimestamp) {
                            remainingPartitions.remove(tp);
                            continue;
                        }

                        consumedCounter.incrementAndGet();

                        String payload = r.value();
                        if (payload != null) {
                            for (String uid : uidsToMatch) {
                                if (payload.contains(uid)) {
                                    ProducerRecord<String, String> pr = new ProducerRecord<>(r.topic(), r.key(), payload);
                                    producer.send(pr, (meta, ex) -> {
                                        if (ex != null) {
                                            System.err.printf("WORKER %s: Failed to republish offset=%d partition=%d: %s%n",
                                                    consumerGroup, r.offset(), r.partition(), ex.getMessage());
                                        }
                                    });
                                    republishedCounter.incrementAndGet();
                                    System.out.printf("🔁 Matched UID '%s' -> Republished offset=%d partition=%d ts=%s%n",
                                            uid, r.offset(), r.partition(), Instant.ofEpochMilli(r.timestamp()));
                                    break; 
                                }
                            }
                        }
                    }

                    for (TopicPartition tp : new HashSet<>(remainingPartitions)) {
                        if (consumer.position(tp) >= endOffsetsExclusive.get(tp)) {
                            remainingPartitions.remove(tp);
                        }
                    }
                }
            } catch (Exception e) {
                System.err.printf("WORKER %s: An error occurred: %s%n", consumerGroup, e.getMessage());
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        }
    }
}