package in.wynk.secret.manager.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

public class JsonExtractor {
    public static String getProgramJson(String jsonContent, String channelId, String programId) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonContent);
            JsonNode channels = rootNode.path("Schedule").path("channel");

            for (JsonNode channel : channels) {
                if (channel.path("channelid").asText().equals(channelId)) {
                    JsonNode programs = channel.path("programme");
                    for (JsonNode program : programs) {
                        if (program.path("programmeid").asText().equals(programId)) {
                            return objectMapper.writeValueAsString(program);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null; // Return null if not found
    }

    public static void main(String[] args) {
        String jsonContent = "{\n" +
            "  \"Schedule\": {\n" +
            "    \"channel\": [\n" +
            "      {\n" +
            "        \"-id\": \"&Pictures\",\n" +
            "        \"channelid\": \"&Pictures\",\n" +
            "        \"channellogourl\": \"\",\n" +
            "        \"genre\": \"Movies\",\n" +
            "        \"channeldisplayname\": \"&Pictures\",\n" +
            "        \"lcn\": \"211\",\n" +
            "        \"price\": \"10\",\n" +
            "        \"programme\": [\n" +
            "          {\n" +
            "            \"programmeid\": \"333450\",\n" +
            "            \"title\": \"Love Breakups Zindagi\",\n" +
            "            \"start\": \"2024-09-09T19:24:00\",\n" +
            "            \"stop\": \"2024-09-09T21:52:00\",\n" +
            "            \"desc\": \"Despite being in committed relationships, Jai and Naina fall in love with each other and find their lives changing.\",\n" +
            "            \"programmeurl\": \"https://d2sqx12cok5m5h.cloudfront.net/PICTURES_LoveBreakupsZindagi_333450.jpg\",\n" +
            "            \"channelid\": \"&Pictures\",\n" +
            "            \"date\": \"20240910\",\n" +
            "            \"episode-num\": \"\",\n" +
            "            \"sub-title\": \"\",\n" +
            "            \"genre\": \"Comedy\",\n" +
            "            \"subgenre\": \"Drama\",\n" +
            "            \"languagename\": \"Hindi\",\n" +
            "            \"isoriginal\": \"False\",\n" +
            "            \"duration\": \"148\",\n" +
            "            \"seasonid\": null,\n" +
            "            \"seasonname\": null,\n" +
            "            \"rating\": \"U/A\",\n" +
            "            \"country_of_origin\": \"India\",\n" +
            "            \"awards\": \"\",\n" +
            "            \"productionhouse\": \"Born Free Entertainment\",\n" +
            "            \"release_date\": \"2011\",\n" +
            "            \"cast\": \"Zayed Khan, Dia Mirza, Cyrus Sahukar, Shabana Azmi, Boman Irani\",\n" +
            "            \"crew\": \"Director: Sahil Sangha; Producer: Zayed Khan; Writer: Sanyukta Shaikh Chawla, Sahil Sangha\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"programmeid\": \"342728\",\n" +
            "            \"title\": \"Tamasha\",\n" +
            "            \"start\": \"2024-09-09T21:52:00\",\n" +
            "            \"stop\": \"2024-09-10T00:00:00\",\n" +
            "            \"desc\": \"A guy loses himself trying to fit in; Tamasha explores his journey back to authenticity.\",\n" +
            "            \"programmeurl\": \"https://d2sqx12cok5m5h.cloudfront.net/AndPicture_Tamasha_342728.jpg\",\n" +
            "            \"channelid\": \"&Pictures\",\n" +
            "            \"date\": \"20240910\",\n" +
            "            \"episode-num\": \"\",\n" +
            "            \"sub-title\": \"\",\n" +
            "            \"genre\": \"Romance\",\n" +
            "            \"subgenre\": \"Comedy\",\n" +
            "            \"languagename\": \"Hindi\",\n" +
            "            \"isoriginal\": \"False\",\n" +
            "            \"duration\": \"128\",\n" +
            "            \"seasonid\": null,\n" +
            "            \"seasonname\": null,\n" +
            "            \"rating\": \"U/A\",\n" +
            "            \"country_of_origin\": \"India\",\n" +
            "            \"awards\": \"\",\n" +
            "            \"productionhouse\": \"Nadiadwala Grandson Entertainment\",\n" +
            "            \"release_date\": \"2015\",\n" +
            "            \"cast\": \"Ranbir Kapoor, Deepika Padukone, Javed Sheikh, Nikhil Bhagat\",\n" +
            "            \"crew\": \"Director: Imtiaz Ali; Producer: Sajid Nadiadwala; Writer: Imtiaz Ali\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"programmeid\": \"62746\",\n" +
            "            \"title\": \"Filler\",\n" +
            "            \"start\": \"2024-09-10T00:00:00\",\n" +
            "            \"stop\": \"2024-09-10T00:07:00\",\n" +
            "            \"desc\": \"Watch promos, music, and fillers back to back from the movies or shows.\",\n" +
            "            \"programmeurl\": \"https://d2sqx12cok5m5h.cloudfront.net/PICTURES_Filler_62746.jpg\",\n" +
            "            \"channelid\": \"&Pictures\",\n" +
            "            \"date\": \"20240910\",\n" +
            "            \"episode-num\": \"\",\n" +
            "            \"sub-title\": \"\",\n" +
            "            \"genre\": \"Other\",\n" +
            "            \"subgenre\": \"\",\n" +
            "            \"languagename\": \"Hindi\",\n" +
            "            \"isoriginal\": \"False\",\n" +
            "            \"duration\": \"7\",\n" +
            "            \"seasonid\": null,\n" +
            "            \"seasonname\": null,\n" +
            "            \"rating\": \"U\",\n" +
            "            \"country_of_origin\": \"\",\n" +
            "            \"awards\": \"\",\n" +
            "            \"productionhouse\": \"\",\n" +
            "            \"release_date\": \"0\",\n" +
            "            \"cast\": \"\",\n" +
            "            \"crew\": \"\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"programmeid\": \"489665\",\n" +
            "            \"title\": \"Great Grand Masti\",\n" +
            "            \"start\": \"2024-09-17T05:36:00\",\n" +
            "            \"stop\": \"2024-09-17T07:44:00\",\n" +
            "            \"desc\": \"Three friends travel to a small town where they meet Ragini, a beautiful woman. However, they soon realise that she has brought nothing but trouble in their lives.\",\n" +
            "            \"programmeurl\": \"https://d2sqx12cok5m5h.cloudfront.net/PICTURES_GreatGrandMasti_489665.jpg\",\n" +
            "            \"channelid\": \"&Pictures\",\n" +
            "            \"date\": \"20240917\",\n" +
            "            \"episode-num\": \"\",\n" +
            "            \"sub-title\": \"\",\n" +
            "            \"genre\": \"Comedy\",\n" +
            "            \"subgenre\": \"Drama\",\n" +
            "            \"languagename\": \"Hindi\",\n" +
            "            \"isoriginal\": \"False\",\n" +
            "            \"duration\": \"128\",\n" +
            "            \"seasonid\": null,\n" +
            "            \"seasonname\": null,\n" +
            "            \"rating\": \"A\",\n" +
            "            \"country_of_origin\": \"India\",\n" +
            "            \"awards\": \"\",\n" +
            "            \"productionhouse\": \"Balaji Motion Pictures, Maruti International, Sri Adhikari Brothers\",\n" +
            "            \"release_date\": \"2016\",\n" +
            "            \"cast\": \"Urvashi Rautela, Riteish Deshmukh, Vivek Oberoi, Aftab Shivdasani\",\n" +
            "            \"crew\": \"Director: Indra Kumar; Producer: Sameer Nair, Aman Gill, Ashok Thakeria, Markand Adhikari, Anand Pandit; Writer: Tushar Hiranandani\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"programmeid\": \"83685\",\n" +
            "            \"title\": \"Maine Pyar Kiya\",\n" +
            "            \"start\": \"2024-09-17T07:44:00\",\n" +
            "            \"stop\": \"2024-09-17T11:44:00\",\n" +
            "            \"desc\": \"Prem and Suman fall in love only to be torn apart by family clashes. Will Prem prove himself worthy of Suman's love?\",\n" +
            "            \"programmeurl\": \"https://d2sqx12cok5m5h.cloudfront.net/AndPicture_MainePyarKiya_83685.jpg\",\n" +
            "            \"channelid\": \"&Pictures\",\n" +
            "            \"date\": \"20240917\",\n" +
            "            \"episode-num\": \"\",\n" +
            "            \"sub-title\": \"\",\n" +
            "            \"genre\": \"Romance\",\n" +
            "            \"subgenre\": \"Family\",\n" +
            "            \"languagename\": \"Hindi\",\n" +
            "            \"isoriginal\": \"False\",\n" +
            "            \"duration\": \"240\",\n" +
            "            \"seasonid\": null,\n" +
            "            \"seasonname\": null,\n" +
            "            \"rating\": \"U\",\n" +
            "            \"country_of_origin\": \"India\",\n" +
            "            \"awards\": \"Filmfare Awards:Best Film: Tarachand Barjatya Best Music DirectorBest Lyricist: Asad Bhopali Best Playback Singer: Balasubramaniam S.P. \",\n" +
            "            \"productionhouse\": \"Rajshri Productions\",\n" +
            "            \"release_date\": \"1989\",\n" +
            "            \"cast\": \"Salman Khan, Bhagyashree, Alok Nath, Rajeev Verma\",\n" +
            "            \"crew\": \"Director: Sooraj R. Barjatya; Producer: Tarachand Barjatya; Writer: Sooraj Barjatya\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"programmeid\": \"247344\",\n" +
            "            \"title\": \"Lingaa\",\n" +
            "            \"start\": \"2024-09-17T11:44:00\",\n" +
            "            \"stop\": \"2024-09-17T14:30:00\",\n" +
            "            \"desc\": \"Lingaa transforms into a hero, saving the dam his grandfather built.\",\n" +
            "            \"programmeurl\": \"https://d2sqx12cok5m5h.cloudfront.net/AndPicture_Lingaa_247344.jpg\",\n" +
            "            \"channelid\": \"&Pictures\",\n" +
            "            \"date\": \"20240917\",\n" +
            "            \"episode-num\": \"\",\n" +
            "            \"sub-title\": \"\",\n" +
            "            \"genre\": \"Action\",\n" +
            "            \"subgenre\": \"\",\n" +
            "            \"languagename\": \"Hindi\",\n" +
            "            \"isoriginal\": \"False\",\n" +
            "            \"duration\": \"166\",\n" +
            "            \"seasonid\": null,\n" +
            "            \"seasonname\": null,\n" +
            "            \"rating\": \"U\",\n" +
            "            \"country_of_origin\": \"India\",\n" +
            "            \"awards\": \"\",\n" +
            "            \"productionhouse\": \"Rockline Entertainments\",\n" +
            "            \"release_date\": \"2014\",\n" +
            "            \"cast\": \"Rajnikanth, Anushka Shetty, Sonakshi Sinha, Santhanam\",\n" +
            "            \"crew\": \"Director: K. S. Ravikumar; Producer: Rockline Venkatesh; Writer: Pon Kumaran, K.S. Ravikumar\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"programmeid\": \"342519\",\n" +
            "            \"title\": \"Real Tevar\",\n" +
            "            \"start\": \"2024-09-17T14:30:00\",\n" +
            "            \"stop\": \"2024-09-17T17:19:00\",\n" +
            "            \"desc\": \"Story about a rich guy, who believes in doing what his heart says. Though he has everything in life, there is something else he desires to accomplish.\",\n" +
            "            \"programmeurl\": \"https://d2sqx12cok5m5h.cloudfront.net/PICTURES_RealTevar_342519.jpg\",\n" +
            "            \"channelid\": \"&Pictures\",\n" +
            "            \"date\": \"20240917\",\n" +
            "            \"episode-num\": \"\",\n" +
            "            \"sub-title\": \"\",\n" +
            "            \"genre\": \"Action\",\n" +
            "            \"subgenre\": \"\",\n" +
            "            \"languagename\": \"Hindi\",\n" +
            "            \"isoriginal\": \"False\",\n" +
            "            \"duration\": \"169\",\n" +
            "            \"seasonid\": null,\n" +
            "            \"seasonname\": null,\n" +
            "            \"rating\": \"U/A\",\n" +
            "            \"country_of_origin\": \"India\",\n" +
            "            \"awards\": \"\",\n" +
            "            \"productionhouse\": \"G.Mahesh BabuEntertainment, MythriMovieMakers\",\n" +
            "            \"release_date\": \"2015\",\n" +
            "            \"cast\": \"Aamani, Jagapathi Babu, Mahesh Babu, Shruti Haasan\",\n" +
            "            \"crew\": \"Director: Koratalla Siva; Producer: Naveen Yerneni, Yalamanchili Ravi Shankar, C. V. Mohan; Writer: Koratalla Siva\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"programmeid\": \"318212\",\n" +
            "            \"title\": \"Alone\",\n" +
            "            \"start\": \"2024-09-17T17:19:00\",\n" +
            "            \"stop\": \"2024-09-17T20:30:00\",\n" +
            "            \"desc\": \"Two twin sisters who lived by one promise - 'We will always be together. We will never separate'. Later, the ghost of dead haunt the surviving sister.\",\n" +
            "            \"programmeurl\": \"https://d2sqx12cok5m5h.cloudfront.net/PICTURES_Alone_318212.jpg\",\n" +
            "            \"channelid\": \"&Pictures\",\n" +
            "            \"date\": \"20240917\",\n" +
            "            \"episode-num\": \"\",\n" +
            "            \"sub-title\": \"\",\n" +
            "            \"genre\": \"Horror\",\n" +
            "            \"subgenre\": \"Romance\",\n" +
            "            \"languagename\": \"Hindi\",\n" +
            "            \"isoriginal\": \"False\",\n" +
            "            \"duration\": \"191\",\n" +
            "            \"seasonid\": null,\n" +
            "            \"seasonname\": null,\n" +
            "            \"rating\": \"A\",\n" +
            "            \"country_of_origin\": \"India\",\n" +
            "            \"awards\": \"\",\n" +
            "            \"productionhouse\": \" Panorama Studios\",\n" +
            "            \"release_date\": \"2015\",\n" +
            "            \"cast\": \"Bipasha Basu, Karan Singh Grover, Zakir Hussain\",\n" +
            "            \"crew\": \"Director: Bhushan Patel; Producer: Kumar Mangat, Pathak, Abhishek Pathak, Pradeep Agarwal,Prashant Sharma; Writer: Shiirshak S. Anand, Shantanu Ray Chhibber, Shagufta Rafique\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"programmeid\": \"193629\",\n" +
            "            \"title\": \"Movie\",\n" +
            "            \"start\": \"2024-09-17T20:30:00\",\n" +
            "            \"stop\": \"2024-09-18T00:45:00\",\n" +
            "            \"desc\": \"Movie name is yet to be announced by this channel.\",\n" +
            "            \"programmeurl\": \"https://d2sqx12cok5m5h.cloudfront.net/PICTURES_Movie_193629.jpg\",\n" +
            "            \"channelid\": \"&Pictures\",\n" +
            "            \"date\": \"20240918\",\n" +
            "            \"episode-num\": \"\",\n" +
            "            \"sub-title\": \"\",\n" +
            "            \"genre\": \"Movie\",\n" +
            "            \"subgenre\": \"\",\n" +
            "            \"languagename\": \"Hindi\",\n" +
            "            \"isoriginal\": \"False\",\n" +
            "            \"duration\": \"255\",\n" +
            "            \"seasonid\": null,\n" +
            "            \"seasonname\": null,\n" +
            "            \"rating\": \"U\",\n" +
            "            \"country_of_origin\": \"\",\n" +
            "            \"awards\": \"\",\n" +
            "            \"productionhouse\": \"\",\n" +
            "            \"release_date\": \"\",\n" +
            "            \"cast\": \"Chris Pratt, Anya Taylor-Joy, Charlie Day, Jack Black\",\n" +
            "            \"crew\": \"\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"programmeid\": \"193629\",\n" +
            "            \"title\": \"Movie\",\n" +
            "            \"start\": \"2024-09-18T00:45:00\",\n" +
            "            \"stop\": \"2024-09-18T02:31:00\",\n" +
            "            \"desc\": \"Movie name is yet to be announced by this channel.\",\n" +
            "            \"programmeurl\": \"https://d2sqx12cok5m5h.cloudfront.net/PICTURES_Movie_193629.jpg\",\n" +
            "            \"channelid\": \"&Pictures\",\n" +
            "            \"date\": \"20240918\",\n" +
            "            \"episode-num\": \"\",\n" +
            "            \"sub-title\": \"\",\n" +
            "            \"genre\": \"Movie\",\n" +
            "            \"subgenre\": \"\",\n" +
            "            \"languagename\": \"Hindi\",\n" +
            "            \"isoriginal\": \"False\",\n" +
            "            \"duration\": \"106\",\n" +
            "            \"seasonid\": null,\n" +
            "            \"seasonname\": null,\n" +
            "            \"rating\": \"U\",\n" +
            "            \"country_of_origin\": \"\",\n" +
            "            \"awards\": \"\",\n" +
            "            \"productionhouse\": \"\",\n" +
            "            \"release_date\": \"\",\n" +
            "            \"cast\": \"Chris Pratt, Anya Taylor-Joy, Charlie Day, Jack Black\",\n" +
            "            \"crew\": \"\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"programmeid\": \"1481560\",\n" +
            "            \"title\": \"Phone Bhoot\",\n" +
            "            \"start\": \"2024-09-18T02:31:00\",\n" +
            "            \"stop\": \"2024-09-18T05:11:00\",\n" +
            "            \"desc\": \"Inspired by stories from their childhood, two young men create a business around capturing ghosts. However, their endeavour turns dangerous when they must fulfil their promise to a poltergeist.\",\n" +
            "            \"programmeurl\": \"https://d2sqx12cok5m5h.cloudfront.net/PICTURES_PhoneBhoot_1481560.jpg\",\n" +
            "            \"channelid\": \"&Pictures\",\n" +
            "            \"date\": \"20240918\",\n" +
            "            \"episode-num\": \"\",\n" +
            "            \"sub-title\": \"\",\n" +
            "            \"genre\": \"Comedy\",\n" +
            "            \"subgenre\": \"Horror\",\n" +
            "            \"languagename\": \"Hindi\",\n" +
            "            \"isoriginal\": \"False\",\n" +
            "            \"duration\": \"160\",\n" +
            "            \"seasonid\": null,\n" +
            "            \"seasonname\": null,\n" +
            "            \"rating\": \"U/A\",\n" +
            "            \"country_of_origin\": \"India\",\n" +
            "            \"awards\": \"\",\n" +
            "            \"productionhouse\": \"Excel Entertainment\",\n" +
            "            \"release_date\": \"2022\",\n" +
            "            \"cast\": \"Katrina Kaif, Ishaan Khatter, Siddhant Chaturvedi, Naufal Azmir Khan\",\n" +
            "            \"crew\": \"Director: Gurmmeet Singh; Producer: Ritesh Sidhwani, Farhan Akhtar; Writer: Ravi Shankaran, Jasvinder Singh Bath\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"programmeid\": \"327133\",\n" +
            "            \"title\": \"Jigarwala No.1\",\n" +
            "            \"start\": \"2024-09-18T05:11:00\",\n" +
            "            \"stop\": \"2024-09-18T07:56:00\",\n" +
            "            \"desc\": \"Dr Robin comes to India to recover his land from a gangster. He is persuaded by a writer who has a hidden agenda.\",\n" +
            "            \"programmeurl\": \"https://d2sqx12cok5m5h.cloudfront.net/PICTURES_JigarwalaNo1_327133.jpg\",\n" +
            "            \"channelid\": \"&Pictures\",\n" +
            "            \"date\": \"20240918\",\n" +
            "            \"episode-num\": \"\",\n" +
            "            \"sub-title\": \"\",\n" +
            "            \"genre\": \"Action\",\n" +
            "            \"subgenre\": \"\",\n" +
            "            \"languagename\": \"Hindi\",\n" +
            "            \"isoriginal\": \"False\",\n" +
            "            \"duration\": \"165\",\n" +
            "            \"seasonid\": null,\n" +
            "            \"seasonname\": null,\n" +
            "            \"rating\": \"U/A\",\n" +
            "            \"country_of_origin\": \"India\",\n" +
            "            \"awards\": \"\",\n" +
            "            \"productionhouse\": \"N.T.R. Arts\",\n" +
            "            \"release_date\": \"2015\",\n" +
            "            \"cast\": \"Ravi Teja, Rakul Preet Singh, Brahmanandam, Tanikella Bharani\",\n" +
            "            \"crew\": \"Director: Surender Reddy, Gogu; Producer: Nandamuri Kalyan Ram; Writer: Vakkantham Vamsi, Surender Reddy, Prasad Rishi\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"programmeid\": \"83686\",\n" +
            "            \"title\": \"Mr. India\",\n" +
            "            \"start\": \"2024-09-18T07:56:00\",\n" +
            "            \"stop\": \"2024-09-18T11:33:00\",\n" +
            "            \"desc\": \"A poor man takes orphans into his home. After discovering his father's invisibility device, he fights to save his children.\",\n" +
            "            \"programmeurl\": \"https://d2sqx12cok5m5h.cloudfront.net/PICTURES_MrIndia_83686.jpg\",\n" +
            "            \"channelid\": \"&Pictures\",\n" +
            "            \"date\": \"20240918\",\n" +
            "            \"episode-num\": \"\",\n" +
            "            \"sub-title\": \"\",\n" +
            "            \"genre\": \"Action\",\n" +
            "            \"subgenre\": \"SciFi\",\n" +
            "            \"languagename\": \"Hindi\",\n" +
            "            \"isoriginal\": \"False\",\n" +
            "            \"duration\": \"217\",\n" +
            "            \"seasonid\": null,\n" +
            "            \"seasonname\": null,\n" +
            "            \"rating\": \"U\",\n" +
            "            \"country_of_origin\": \"India\",\n" +
            "            \"awards\": \"\",\n" +
            "            \"productionhouse\": \"Narsimha Enterprises\",\n" +
            "            \"release_date\": \"1987\",\n" +
            "            \"cast\": \"Anil Kapoor, Sridevi Kapoor, Amrish Puri, Satish Kaushik\",\n" +
            "            \"crew\": \"Director: Shekhar Kapur; Producer: Boney Kapoor, Surinder Kapoor; Writer: Javed Akhtar, Salim Khan \"\n" +
            "          },\n" +
            "          {\n" +
            "            \"programmeid\": \"907601\",\n" +
            "            \"title\": \"No1 Businessman\",\n" +
            "            \"start\": \"2024-09-18T11:33:00\",\n" +
            "            \"stop\": \"2024-09-18T14:00:00\",\n" +
            "            \"desc\": \"A man visits Mumbai with the aim of ruling the city. He turns into a don and manages to become a businessman. His intentions are not what they seem.\",\n" +
            "            \"programmeurl\": \"https://d2sqx12cok5m5h.cloudfront.net/PICTURES_No1Businessman_907601.jpg\",\n" +
            "            \"channelid\": \"&Pictures\",\n" +
            "            \"date\": \"20240918\",\n" +
            "            \"episode-num\": \"\",\n" +
            "            \"sub-title\": \"\",\n" +
            "            \"genre\": \"Action\",\n" +
            "            \"subgenre\": \"\",\n" +
            "            \"languagename\": \"Hindi\",\n" +
            "            \"isoriginal\": \"False\",\n" +
            "            \"duration\": \"147\",\n" +
            "            \"seasonid\": null,\n" +
            "            \"seasonname\": null,\n" +
            "            \"rating\": \"UA\",\n" +
            "            \"country_of_origin\": \"\",\n" +
            "            \"awards\": \"\",\n" +
            "            \"productionhouse\": \"\",\n" +
            "            \"release_date\": \"\",\n" +
            "            \"cast\": \"\",\n" +
            "            \"crew\": \"\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"programmeid\": \"954233\",\n" +
            "            \"title\": \"Dream Girl\",\n" +
            "            \"start\": \"2024-09-18T14:00:00\",\n" +
            "            \"stop\": \"2024-09-18T16:39:00\",\n" +
            "            \"desc\": \"Karamveer gets a job at a call centre due to his ability to talk in a woman's voice. He garners a big fan following but soon gets in trouble with his overzealous clients.\",\n" +
            "            \"programmeurl\": \"https://d2sqx12cok5m5h.cloudfront.net/PICTURES_DreamGirl_954233.jpg\",\n" +
            "            \"channelid\": \"&Pictures\",\n" +
            "            \"date\": \"20240918\",\n" +
            "            \"episode-num\": \"\",\n" +
            "            \"sub-title\": \"\",\n" +
            "            \"genre\": \"Comedy\",\n" +
            "            \"subgenre\": \"Romance\",\n" +
            "            \"languagename\": \"Hindi\",\n" +
            "            \"isoriginal\": \"False\",\n" +
            "            \"duration\": \"159\",\n" +
            "            \"seasonid\": null,\n" +
            "            \"seasonname\": null,\n" +
            "            \"rating\": \"U/A\",\n" +
            "            \"country_of_origin\": \"India\",\n" +
            "            \"awards\": \"\",\n" +
            "            \"productionhouse\": \"Balaji Motion Pictures, ALT Entertainment\",\n" +
            "            \"release_date\": \"2019\",\n" +
            "            \"cast\": \"Ayushmann Khurrana, Nushrat Bharucha, Annu Kapoor, Manjot Singh\",\n" +
            "            \"crew\": \"Director: Raaj Shaandilyaa; Producer: Ekta Kapoor, Shobha Kapoor; Writer: Nirmaan D Singh, Raaj Shaandilyaa\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"programmeid\": \"1191829\",\n" +
            "            \"title\": \"Radhe\",\n" +
            "            \"start\": \"2024-09-18T16:39:00\",\n" +
            "            \"stop\": \"2024-09-18T20:30:00\",\n" +
            "            \"desc\": \"After taking the dreaded gangster Gani Bhai, ACP Rajveer Shikawat aka Radhe goes on a manhunt to find the wealthiest man of the town secretly running a crime syndicate.\",\n" +
            "            \"programmeurl\": \"https://d2sqx12cok5m5h.cloudfront.net/PICTURES_Radhe_1191829.jpg\",\n" +
            "            \"channelid\": \"&Pictures\",\n" +
            "            \"date\": \"20240918\",\n" +
            "            \"episode-num\": \"\",\n" +
            "            \"sub-title\": \"\",\n" +
            "            \"genre\": \"Action\",\n" +
            "            \"subgenre\": \"Thriller\",\n" +
            "            \"languagename\": \"Hindi\",\n" +
            "            \"isoriginal\": \"False\",\n" +
            "            \"duration\": \"231\",\n" +
            "            \"seasonid\": null,\n" +
            "            \"seasonname\": null,\n" +
            "            \"rating\": \"U/A\",\n" +
            "            \"country_of_origin\": \"India\",\n" +
            "            \"awards\": \"\",\n" +
            "            \"productionhouse\": \"Zee Studios, Salman Khan Films, Sohail Khan Productions, Reel Life Production Private Limited\",\n" +
            "            \"release_date\": \"2021\",\n" +
            "            \"cast\": \"Salman Khan, Disha Patani, Randeep Hooda, Jackie Shroff\",\n" +
            "            \"crew\": \"Director: Prabhu Deva; Producer: Salman Khan, Sohail Khan,  Atul Agnihotri, Nikhil Namit; Writer: A.C Mugil, Vijay Maurya\"\n" +
            "          }\n" +
            "        ]\n" +
            "      }\n" +
            "    ]\n" +
            "  }\n" +
            "}";
        String result = getProgramJson(jsonContent, "&Pictures", "333450");
        System.out.println(result);
    }
}
