package in.wynk.secret.manager.utils.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class KafkaMessageProducer {

    public static void main(String[] args) {
//        String bootstrapServers = "10.161.24.22:9092,10.161.24.24:9092,10.161.24.23:9092";
//        String topicName = "xstream-wcf-events";
//        String username = "appuser";
//        String password = "uJK67AUDI1Ax";

//        String bootstrapServers = "10.164.88.21:9092,10.164.88.22:9092,10.164.88.23:9092";
//        String topicName = "msp-processing-prd-app";
//        String username = "appuser";
//        String password = "uJK67AUDI1Ax";

        String bootstrapServers = "10.161.24.77:9092";
        String topicName = "Topic-test";
        String username = "appuser";
        String password = "uJK67AUDI1Ax";
        String key = "825db3c3-e885-4bc3-b026-e051169c4179";
        String value = """
                {
                	"schema": {
                		"type": "struct",
                		"fields": [
                			{
                				"type": "string",
                				"optional": true,
                				"name": "io.debezium.data.Json",
                				"version": 1,
                				"field": "before"
                			},
                			{
                				"type": "string",
                				"optional": true,
                				"name": "io.debezium.data.Json",
                				"version": 1,
                				"field": "after"
                			},
                			{
                				"type": "struct",
                				"fields": [
                					{
                						"type": "array",
                						"items": {
                							"type": "string",
                							"optional": false
                						},
                						"optional": true,
                						"field": "removedFields"
                					},
                					{
                						"type": "string",
                						"optional": true,
                						"name": "io.debezium.data.Json",
                						"version": 1,
                						"field": "updatedFields"
                					},
                					{
                						"type": "array",
                						"items": {
                							"type": "struct",
                							"fields": [
                								{
                									"type": "string",
                									"optional": false,
                									"field": "field"
                								},
                								{
                									"type": "int32",
                									"optional": false,
                									"field": "size"
                								}
                							],
                							"optional": false,
                							"name": "io.debezium.connector.mongodb.changestream.truncatedarray",
                							"version": 1
                						},
                						"optional": true,
                						"field": "truncatedArrays"
                					}
                				],
                				"optional": true,
                				"name": "io.debezium.connector.mongodb.changestream.updatedescription",
                				"version": 1,
                				"field": "updateDescription"
                			},
                			{
                				"type": "struct",
                				"fields": [
                					{
                						"type": "string",
                						"optional": false,
                						"field": "version"
                					},
                					{
                						"type": "string",
                						"optional": false,
                						"field": "connector"
                					},
                					{
                						"type": "string",
                						"optional": false,
                						"field": "name"
                					},
                					{
                						"type": "int64",
                						"optional": false,
                						"field": "ts_ms"
                					},
                					{
                						"type": "string",
                						"optional": true,
                						"name": "io.debezium.data.Enum",
                						"version": 1,
                						"parameters": {
                							"allowed": "true,last,false,incremental"
                						},
                						"default": "false",
                						"field": "snapshot"
                					},
                					{
                						"type": "string",
                						"optional": false,
                						"field": "db"
                					},
                					{
                						"type": "string",
                						"optional": true,
                						"field": "sequence"
                					},
                					{
                						"type": "string",
                						"optional": false,
                						"field": "rs"
                					},
                					{
                						"type": "string",
                						"optional": false,
                						"field": "collection"
                					},
                					{
                						"type": "int32",
                						"optional": false,
                						"field": "ord"
                					},
                					{
                						"type": "string",
                						"optional": true,
                						"field": "lsid"
                					},
                					{
                						"type": "int64",
                						"optional": true,
                						"field": "txnNumber"
                					},
                					{
                						"type": "int64",
                						"optional": true,
                						"field": "wallTime"
                					}
                				],
                				"optional": false,
                				"name": "io.debezium.connector.mongo.Source",
                				"field": "source"
                			},
                			{
                				"type": "string",
                				"optional": true,
                				"field": "op"
                			},
                			{
                				"type": "int64",
                				"optional": true,
                				"field": "ts_ms"
                			},
                			{
                				"type": "struct",
                				"fields": [
                					{
                						"type": "string",
                						"optional": false,
                						"field": "id"
                					},
                					{
                						"type": "int64",
                						"optional": false,
                						"field": "total_order"
                					},
                					{
                						"type": "int64",
                						"optional": false,
                						"field": "data_collection_order"
                					}
                				],
                				"optional": true,
                				"name": "event.block",
                				"version": 1,
                				"field": "transaction"
                			}
                		],
                		"optional": false,
                		"name": "msp.msp.content.Envelope"
                	},
                	"payload": {
                		"before": null,
                		"after": "{\\"_id\\": {\\"$oid\\": \\"66c81a39795755238d2e8537\\"},\\"name\\": \\"Sarfarosh.mp4\\",\\"pcid\\": \\"66c81a39795755238d2e8537\\",\\"uploadType\\": \\"BULK\\",\\"playableContent\\": {\\"type\\": \\"VOD\\",\\"category\\": \\"VIDEO\\",\\"subcategory\\": \\"MOVIE\\",\\"subcategoryName\\": \\"Movie\\",\\"playable\\": true,\\"_class\\": \\"in.wynk.msp.domain.content.dto.types.Video\\"},\\"partnerId\\": \\"ULTRA\\",\\"vodStatus\\": \\"COMPLETED\\",\\"metadataStatus\\": \\"COMPLETE\\",\\"contentState\\": \\"UPLOADED\\",\\"artworks\\": {\\"PORTRAIT\\": {\\"url\\": \\"https://ultra-assets.streamready.in/assets/ULTRA/MOVIE/66c81a39795755238d2e8537/images/PORTRAIT/PORTRAIT_jIFf-hNaW_Sarfarosh_1200x1600-(1).jpg\\",\\"layoutType\\": {\\"name\\": \\"PORTRAIT\\",\\"fileName\\": \\"jIFf-hNaW_Sarfarosh_1200x1600-(1).jpg\\",\\"displayName\\": \\"Portrait\\",\\"aspectRatio\\": \\"3:4\\",\\"resolution\\": \\"1200*1600\\",\\"pixelDensity\\": \\"300\\",\\"size\\": \\"1690098\\",\\"isMandatory\\": true,\\"validationActionEnabled\\": false},\\"updated\\": {\\"$numberLong\\": \\"1724413295293\\"}},\\"LANDSCAPE_169\\": {\\"url\\": \\"https://ultra-assets.streamready.in/assets/ULTRA/MOVIE/66c81a39795755238d2e8537/images/LANDSCAPE_169/LANDSCAPE_169_tWplzZTpP_Sarfarosh_1920x1080-(2).jpg\\",\\"layoutType\\": {\\"name\\": \\"LANDSCAPE_169\\",\\"fileName\\": \\"tWplzZTpP_Sarfarosh_1920x1080-(2).jpg\\",\\"displayName\\": \\"Landscape\\",\\"aspectRatio\\": \\"16:9\\",\\"resolution\\": \\"1920*1080\\",\\"pixelDensity\\": \\"300\\",\\"size\\": \\"2109204\\",\\"isMandatory\\": true,\\"validationActionEnabled\\": false},\\"updated\\": {\\"$numberLong\\": \\"1724413295293\\"}},\\"FEATURE_BANNER\\": {\\"url\\": \\"https://ultra-assets.streamready.in/assets/ULTRA/MOVIE/66c81a39795755238d2e8537/images/FEATURE_BANNER/FEATURE_BANNER_BF3Ud8ia__Sarfarosh_960x540-(1).jpg\\",\\"layoutType\\": {\\"name\\": \\"FEATURE_BANNER\\",\\"fileName\\": \\"BF3Ud8ia__Sarfarosh_960x540-(1).jpg\\",\\"displayName\\": \\"Feature Banner\\",\\"aspectRatio\\": \\"16:9\\",\\"resolution\\": \\"960*540\\",\\"pixelDensity\\": \\"300\\",\\"size\\": \\"606798\\",\\"isMandatory\\": false,\\"validationActionEnabled\\": false},\\"updated\\": {\\"$numberLong\\": \\"1724413295293\\"}},\\"FEATURE_BANNER_HD\\": {\\"url\\": \\"https://ultra-assets.streamready.in/assets/ULTRA/MOVIE/66c81a39795755238d2e8537/images/FEATURE_BANNER_HD/FEATURE_BANNER_HD_YsxoPZSPc_Sarfarosh_1920x548-(1).jpg\\",\\"layoutType\\": {\\"name\\": \\"FEATURE_BANNER_HD\\",\\"fileName\\": \\"YsxoPZSPc_Sarfarosh_1920x548-(1).jpg\\",\\"displayName\\": \\"Feature Banner HD\\",\\"aspectRatio\\": \\"480:137\\",\\"resolution\\": \\"1920*548\\",\\"pixelDensity\\": \\"300\\",\\"size\\": \\"1060873\\",\\"isMandatory\\": false,\\"validationActionEnabled\\": false},\\"updated\\": {\\"$numberLong\\": \\"1724413295293\\"}},\\"SQUARE\\": {\\"url\\": \\"https://ultra-assets.streamready.in/assets/ULTRA/MOVIE/66c81a39795755238d2e8537/images/SQUARE/SQUARE_3EAl-D6wE_Sarfarosh_1000x1000-(1).jpg\\",\\"layoutType\\": {\\"name\\": \\"SQUARE\\",\\"fileName\\": \\"3EAl-D6wE_Sarfarosh_1000x1000-(1).jpg\\",\\"displayName\\": \\"Square\\",\\"aspectRatio\\": \\"1:1\\",\\"resolution\\": \\"1000*1000\\",\\"pixelDensity\\": \\"300\\",\\"size\\": \\"955916\\",\\"isMandatory\\": false,\\"validationActionEnabled\\": false},\\"updated\\": {\\"$numberLong\\": \\"1724413295293\\"}},\\"SQUARE_HD\\": {\\"url\\": \\"https://ultra-assets.streamready.in/assets/ULTRA/MOVIE/66c81a39795755238d2e8537/images/SQUARE_HD/SQUARE_HD_T8VdzL_D3_Sarfarosh_1000x1000-(1).jpg\\",\\"layoutType\\": {\\"name\\": \\"SQUARE_HD\\",\\"fileName\\": \\"T8VdzL_D3_Sarfarosh_1000x1000-(1).jpg\\",\\"displayName\\": \\"Square\\",\\"aspectRatio\\": \\"1:1\\",\\"resolution\\": \\"1000*1000\\",\\"pixelDensity\\": \\"300\\",\\"size\\": \\"955916\\",\\"isMandatory\\": false,\\"validationActionEnabled\\": false},\\"updated\\": {\\"$numberLong\\": \\"1724413295293\\"}},\\"LANDSCAPE_43\\": {\\"url\\": \\"https://ultra-assets.streamready.in/assets/ULTRA/MOVIE/66c81a39795755238d2e8537/images/LANDSCAPE_43/LANDSCAPE_43_I3vOs3ilK_Sarfarosh_1024x768-(1).jpg\\",\\"layoutType\\": {\\"name\\": \\"LANDSCAPE_43\\",\\"fileName\\": \\"I3vOs3ilK_Sarfarosh_1024x768-(1).jpg\\",\\"displayName\\": \\"Landscape 4:3\\",\\"aspectRatio\\": \\"4:3\\",\\"resolution\\": \\"1024*768\\",\\"pixelDensity\\": \\"300\\",\\"size\\": \\"869379\\",\\"isMandatory\\": false,\\"validationActionEnabled\\": false},\\"updated\\": {\\"$numberLong\\": \\"1724413295293\\"}}},\\"vodInfo\\": {\\"contentBucket\\": \\"wynk-msp-ultra-vod\\",\\"contentDomain\\": \\"https://ultra-streams.streamready.in/\\",\\"assetsBucket\\": \\"wynk-msp-ultra-assets\\",\\"assetsDomain\\": \\"https://ultra-assets.streamready.in/\\",\\"filePath\\": \\"vod/ULTRA/MOVIE/66c81a39795755238d2e8537/\\",\\"vodPath\\": \\"vod/ULTRA/MOVIE/66c81a39795755238d2e8537/\\",\\"renditionsPath\\": \\"renditions/ULTRA/MOVIE/66c81a39795755238d2e8537/\\",\\"streamsPath\\": \\"streams/ULTRA/MOVIE/66c81a39795755238d2e8537/\\",\\"imagePath\\": \\"assets/ULTRA/MOVIE/66c81a39795755238d2e8537/images/\\",\\"subtitlePath\\": \\"assets/ULTRA/MOVIE/66c81a39795755238d2e8537/subtitles/\\",\\"renditions\\": [{\\"name\\": \\"240p\\",\\"resolution\\": \\"426:240\\",\\"videoBitrate\\": \\"600k\\",\\"audioBitrate\\": \\"64k\\",\\"bitrate\\": \\"600k\\"},{\\"name\\": \\"360p\\",\\"resolution\\": \\"640:360\\",\\"videoBitrate\\": \\"1000k\\",\\"audioBitrate\\": \\"96k\\",\\"bitrate\\": \\"1000k\\"},{\\"name\\": \\"480p\\",\\"resolution\\": \\"854:480\\",\\"videoBitrate\\": \\"1600k\\",\\"audioBitrate\\": \\"96k\\",\\"bitrate\\": \\"1600k\\"},{\\"name\\": \\"720p\\",\\"resolution\\": \\"1280:720\\",\\"videoBitrate\\": \\"2400k\\",\\"audioBitrate\\": \\"128k\\",\\"bitrate\\": \\"2400k\\"},{\\"name\\": \\"1080p\\",\\"resolution\\": \\"1920:1080\\",\\"videoBitrate\\": \\"4000k\\",\\"audioBitrate\\": \\"192k\\",\\"bitrate\\": \\"4000k\\"}],\\"renditionsClass\\": \\"Desktop\\",\\"drm\\": true,\\"uploadProgress\\": {\\"progress\\": 100,\\"uploaded\\": \\"14761840194\\",\\"updated\\": {\\"$numberLong\\": \\"1724391779308\\"}},\\"processingProgress\\": {\\"progress\\": 100,\\"uploaded\\": \\"0\\",\\"updated\\": {\\"$numberLong\\": \\"1724404030413\\"}},\\"renditionCount\\": 5,\\"duration\\": 9585,\\"fileSize\\": \\"14761840194\\",\\"isSourcePlaySupported\\": true,\\"uploaderFileName\\": \\"Sarfarosh.mp4\\",\\"uploadedFileName\\": \\"66c81a39795755238d2e8537.mp4\\",\\"taskId\\": \\"66c8216481867956f7558f76\\",\\"transcodingStartTime\\": {\\"$numberLong\\": \\"1724391971821\\"},\\"outputSizes\\": [{\\"streamType\\": \\"HLS\\",\\"resolutionSizes\\": [{\\"resolution\\": \\"240p\\",\\"size\\": {\\"$numberLong\\": \\"864631379\\"},\\"s3path\\": \\"streams/ULTRA/MOVIE/66c81a39795755238d2e8537/hls/426x240/\\"},{\\"resolution\\": \\"360p\\",\\"size\\": {\\"$numberLong\\": \\"1373696604\\"},\\"s3path\\": \\"streams/ULTRA/MOVIE/66c81a39795755238d2e8537/hls/640x360/\\"},{\\"resolution\\": \\"480p\\",\\"size\\": {\\"$numberLong\\": \\"2087212064\\"},\\"s3path\\": \\"streams/ULTRA/MOVIE/66c81a39795755238d2e8537/hls/854x480/\\"},{\\"resolution\\": \\"720p\\",\\"size\\": {\\"$numberLong\\": \\"3082474852\\"},\\"s3path\\": \\"streams/ULTRA/MOVIE/66c81a39795755238d2e8537/hls/1280x720/\\"},{\\"resolution\\": \\"1080p\\",\\"size\\": {\\"$numberLong\\": \\"5135554608\\"},\\"s3path\\": \\"streams/ULTRA/MOVIE/66c81a39795755238d2e8537/hls/1920x1080/\\"},{\\"resolution\\": \\"audio\\",\\"size\\": {\\"$numberLong\\": \\"554\\"},\\"s3path\\": \\"streams/ULTRA/MOVIE/66c81a39795755238d2e8537/hls/\\"}],\\"totalSize\\": {\\"$numberLong\\": \\"12543570061\\"}},{\\"streamType\\": \\"DASH\\",\\"resolutionSizes\\": [{\\"resolution\\": \\"240p\\",\\"size\\": {\\"$numberLong\\": \\"729166660\\"},\\"s3path\\": \\"streams/ULTRA/MOVIE/66c81a39795755238d2e8537/dash/426x240/\\"},{\\"resolution\\": \\"360p\\",\\"size\\": {\\"$numberLong\\": \\"1183525900\\"},\\"s3path\\": \\"streams/ULTRA/MOVIE/66c81a39795755238d2e8537/dash/640x360/\\"},{\\"resolution\\": \\"480p\\",\\"size\\": {\\"$numberLong\\": \\"1881680276\\"},\\"s3path\\": \\"streams/ULTRA/MOVIE/66c81a39795755238d2e8537/dash/854x480/\\"},{\\"resolution\\": \\"720p\\",\\"size\\": {\\"$numberLong\\": \\"2825012540\\"},\\"s3path\\": \\"streams/ULTRA/MOVIE/66c81a39795755238d2e8537/dash/1280x720/\\"},{\\"resolution\\": \\"1080p\\",\\"size\\": {\\"$numberLong\\": \\"4751227550\\"},\\"s3path\\": \\"streams/ULTRA/MOVIE/66c81a39795755238d2e8537/dash/1920x1080/\\"},{\\"resolution\\": \\"audio\\",\\"size\\": {\\"$numberLong\\": \\"619966910\\"},\\"s3path\\": \\"streams/ULTRA/MOVIE/66c81a39795755238d2e8537/dash/\\"}],\\"totalSize\\": {\\"$numberLong\\": \\"11990579836\\"}}],\\"streamsPathSize\\": {\\"$numberLong\\": \\"24534149897\\"},\\"transcodingEndTime\\": {\\"$numberLong\\": \\"1724404030191\\"},\\"imagePathSize\\": {\\"$numberLong\\": \\"8315103\\"},\\"subtitlePathSize\\": {\\"$numberLong\\": \\"276230\\"}},\\"distribution\\": {\\"spartnerSubscriptions\\": {\\"ULTRA-XSTREAM\\": [\\"610396230d48626ca75d0718\\"]},\\"samplingAllowed\\": {},\\"distributionPublished\\": true},\\"streamingInfo\\": {\\"yesterdayViews\\": {\\"$numberLong\\": \\"0\\"},\\"dayBeforeYesterdayViews\\": {\\"$numberLong\\": \\"0\\"}},\\"metadata\\": {\\"audioLanguage\\": [{\\"dropdownId\\": \\"hin\\",\\"name\\": \\"Hindi (HIN)\\",\\"additionalProperties\\": {\\"isoCode\\": \\"hin\\",\\"scope\\": \\"I\\",\\"isoCode2T\\": \\"hin\\",\\"isoCode1\\": \\"hi\\",\\"comment\\": \\"\\",\\"type\\": \\"L\\",\\"isoCode2B\\": \\"hin\\"},\\"_class\\": \\"in.wynk.msp.dto.cms.content.DropdownEntry\\"}],\\"genre\\": [{\\"dropdownId\\": \\"Action\\",\\"name\\": \\"Action\\",\\"additionalProperties\\": {},\\"_class\\": \\"in.wynk.msp.dto.cms.content.DropdownEntry\\"},{\\"dropdownId\\": \\"Drama\\",\\"name\\": \\"Drama\\",\\"additionalProperties\\": {},\\"_class\\": \\"in.wynk.msp.dto.cms.content.DropdownEntry\\"},{\\"dropdownId\\": \\"Thriller\\",\\"name\\": \\"Thriller\\",\\"additionalProperties\\": {},\\"_class\\": \\"in.wynk.msp.dto.cms.content.DropdownEntry\\"}],\\"ccRating\\": [{\\"dropdownId\\": \\"U/A-13+\\",\\"name\\": \\"Suitable for ages 13 and above\\",\\"additionalProperties\\": {},\\"_class\\": \\"in.wynk.msp.dto.cms.content.DropdownEntry\\"}],\\"downloadable\\": \\"yes\\",\\"cast\\": [{\\"_id\\": {\\"$oid\\": \\"6214cffaf83bd744e8abc58d\\"},\\"name\\": \\"Aamir Khan\\",\\"popularity\\": 0.0,\\"additionalProperties\\": {\\"tmdbId\\": 52763,\\"imageUrl\\": \\"https://people.streamready.in/images/bI3yxh4FzKL2hHYS4nbzcDMWtZs.jpg\\"},\\"_class\\": \\"in.wynk.msp.domain.content.entity.metadata.InformativeDropDownBoxValue\\"},{\\"_id\\": {\\"$oid\\": \\"6214cef9f83bd744e8ab96ac\\"},\\"name\\": \\"Sonali Bendre\\",\\"popularity\\": 0.0,\\"additionalProperties\\": {\\"tmdbId\\": 35754,\\"imageUrl\\": \\"https://people.streamready.in/images/4xvVvT9RyLvOa2hQjecIhYuSPjx.jpg\\"},\\"_class\\": \\"in.wynk.msp.domain.content.entity.metadata.InformativeDropDownBoxValue\\"},{\\"_id\\": {\\"$oid\\": \\"6214ce00f83bd744e8ab3868\\"},\\"name\\": \\"Naseeruddin Shah\\",\\"popularity\\": 0.0,\\"additionalProperties\\": {\\"tmdbId\\": 6497,\\"imageUrl\\": \\"https://people.streamready.in/images/qgkbFx0LXjLjGvlI37E38uRtsVo.jpg\\"},\\"_class\\": \\"in.wynk.msp.domain.content.entity.metadata.InformativeDropDownBoxValue\\"},{\\"_id\\": {\\"$oid\\": \\"6214d261f83bd744e8ac3e4a\\"},\\"name\\": \\"Nawazuddin Siddiqui\\",\\"popularity\\": 0.0,\\"additionalProperties\\": {\\"tmdbId\\": 85047,\\"imageUrl\\": \\"https://people.streamready.in/images/9eeCApyd3fTrLVeKSVs6ZEVLi4l.jpg\\"},\\"_class\\": \\"in.wynk.msp.domain.content.entity.metadata.InformativeDropDownBoxValue\\"},{\\"_id\\": {\\"$oid\\": \\"6216205f3aaba95b337f715d\\"},\\"name\\": \\"Mukesh Rishi\\",\\"popularity\\": 0.0,\\"additionalProperties\\": {\\"tmdbId\\": 585404,\\"imageUrl\\": \\"https://people.streamready.in/images/3MogbprwvLPXk2ZaVPz4DHnNk7J.jpg\\"},\\"_class\\": \\"in.wynk.msp.domain.content.entity.metadata.InformativeDropDownBoxValue\\"},{\\"_id\\": {\\"$oid\\": \\"62161fe93aaba95b337e5d9c\\"},\\"name\\": \\"Makrand Deshpande\\",\\"popularity\\": 0.0,\\"additionalProperties\\": {\\"tmdbId\\": 130990,\\"imageUrl\\": \\"https://people.streamready.in/images/aGA9mjohVWWTeTmutXikSSCYq9x.jpg\\"},\\"_class\\": \\"in.wynk.msp.domain.content.entity.metadata.InformativeDropDownBoxValue\\"},{\\"_id\\": {\\"$oid\\": \\"6214d263f83bd744e8ac3e80\\"},\\"name\\": \\"Manoj Joshi\\",\\"popularity\\": 0.0,\\"additionalProperties\\": {\\"tmdbId\\": 86018,\\"imageUrl\\": \\"https://people.streamready.in/images/6uCyRDLYx4INHvi4HyfarXEwWXL.jpg\\"},\\"_class\\": \\"in.wynk.msp.domain.content.entity.metadata.InformativeDropDownBoxValue\\"}],\\"description\\": {\\"Short\\": \\"Sarfarosh Is An Award Winning Bollywood Action Thriller Hindi Movie, Directed By John Mathew Matthan, Starring Aamir Khan, Naseeruddin Shah, Sonali Bendre And Mukesh Rishi In The Lead Roles.\\",\\"longSameAsShort\\": false,\\"Long\\": \\"Sarfarosh Is An Award Winning Bollywood Action Thriller Hindi Movie, Directed By John Mathew Matthan, Starring Aamir Khan, Naseeruddin Shah, Sonali Bendre And Mukesh Rishi In The Lead Roles. After His Brother Is Killed And Father Severely Injured By Terrorists, A Young Medical Student Quits His Studies To Join The Indian Police Service To Wipe Out The Terrorists.\\\\n\\",\\"_class\\": \\"in.wynk.msp.domain.content.entity.metadata.LinkedTextAreaInputBoxValue\\"},\\"directors\\": [{\\"_id\\": {\\"$oid\\": \\"621772376373610b2d3a8cc1\\"},\\"name\\": \\"John Mathew Matthan\\",\\"popularity\\": 0.0,\\"additionalProperties\\": {\\"tmdbId\\": 1141406,\\"imageUrl\\": \\"https://people.streamready.in/images/yDkAY6uQ4tMaJbb1Crc8z300i5b.jpg\\"},\\"_class\\": \\"in.wynk.msp.domain.content.entity.metadata.InformativeDropDownBoxValue\\"}],\\"expiryDate\\": {\\"timestamp\\": {\\"$numberLong\\": \\"1774895400\\"},\\"justYear\\": false,\\"_class\\": \\"in.wynk.msp.domain.content.entity.metadata.DateInputBoxValue\\"},\\"releaseDate\\": {\\"timestamp\\": {\\"$numberLong\\": \\"925410600\\"},\\"justYear\\": false,\\"_class\\": \\"in.wynk.msp.domain.content.entity.metadata.DateInputBoxValue\\"},\\"title\\": \\"Sarfarosh\\",\\"trailers\\": [{\\"_id\\": {\\"$oid\\": \\"66c8644c81867956f7558fbf\\"},\\"imageUrl\\": \\"https://ultra-assets.streamready.in/assets/ULTRA/PROMO/66c8644c81867956f7558fbf/images/THUMBNAIL/THUMBNAIL_128_72_aavRPZ6U_image.jpg\\",\\"name\\": \\"Sarfarosh Trailer\\",\\"subtitle\\": \\"VIDEO (PROMO)\\",\\"playableContent\\": {\\"type\\": \\"VOD\\",\\"category\\": \\"VIDEO\\",\\"subcategory\\": \\"PROMO\\",\\"subcategoryName\\": \\"Promos\\",\\"playable\\": true,\\"_class\\": \\"in.wynk.msp.domain.content.dto.types.Video\\"},\\"partnerId\\": \\"ULTRA\\",\\"audioLanguage\\": [{\\"isoCode\\": \\"hin\\",\\"isoCode2B\\": \\"hin\\",\\"isoCode2T\\": \\"hin\\",\\"isoCode1\\": \\"hi\\",\\"scope\\": \\"I\\",\\"type\\": \\"L\\",\\"name\\": \\"Hindi\\",\\"comment\\": \\"\\"}],\\"additionalProperties\\": {},\\"_class\\": \\"in.wynk.msp.domain.content.entity.metadata.ContentDropdownBoxValue\\"}],\\"releaseYear\\": {\\"timestamp\\": {\\"$numberLong\\": \\"915129000\\"},\\"year\\": \\"1999\\",\\"justYear\\": true,\\"_class\\": \\"in.wynk.msp.domain.content.entity.metadata.DateInputBoxValue\\"},\\"xyz\\": \\"3\\",\\"subtitles\\": [],\\"skipCredits\\": {\\"$numberLong\\": \\"90\\"},\\"skipIntro\\": {\\"$numberLong\\": \\"201\\"},\\"tx\\": 25},\\"ua\\": {\\"$numberLong\\": \\"1769002038432\\"},\\"ca\\": {\\"$numberLong\\": \\"1724389945775\\"},\\"cb\\": \\"60cc7a9aba15490bbe103588\\",\\"updateHistory\\": [{\\"ts\\": {\\"$numberLong\\": \\"1724413709636\\"},\\"uid\\": \\"60cc7a9aba15490bbe103588\\"},{\\"ts\\": {\\"$numberLong\\": \\"1725530946000\\"},\\"uid\\": \\"60cc7a9aba15490bbe103588\\"},{\\"ts\\": {\\"$numberLong\\": \\"1729592769552\\"},\\"uid\\": \\"64e712fbc62751079804abc7\\"},{\\"ts\\": {\\"$numberLong\\": \\"1743665423200\\"},\\"uid\\": \\"60cc7a9aba15490bbe103588\\"},{\\"ts\\": {\\"$numberLong\\": \\"1743686023907\\"},\\"uid\\": \\"60cc7a9aba15490bbe103588\\"}],\\"creator\\": {\\"ts\\": {\\"$numberLong\\": \\"1724389945775\\"},\\"uid\\": \\"60cc7a9aba15490bbe103588\\"},\\"_class\\": \\"in.wynk.msp.domain.content.entity.Content\\",\\"thumbnails\\": {\\"THUMBNAIL\\": {\\"url\\": \\"https://ultra-assets.streamready.in/assets/ULTRA/MOVIE/66c81a39795755238d2e8537/images/THUMBNAIL/THUMBNAIL_6sZScEOB_image.jpg\\",\\"fileName\\": \\"THUMBNAIL_6sZScEOB_image.jpg\\",\\"layoutType\\": {\\"name\\": \\"THUMBNAIL\\",\\"displayName\\": \\"Thumbnail\\",\\"isMandatory\\": false,\\"validationActionEnabled\\": false},\\"updated\\": {\\"$numberLong\\": \\"1724391781247\\"}},\\"THUMBNAIL_128_72\\": {\\"url\\": \\"https://ultra-assets.streamready.in/assets/ULTRA/MOVIE/66c81a39795755238d2e8537/images/THUMBNAIL/THUMBNAIL_128_72_lzBf6Wxg_image.jpg\\",\\"fileName\\": \\"THUMBNAIL_128_72_lzBf6Wxg_image.jpg\\",\\"layoutType\\": {\\"name\\": \\"THUMBNAIL_128_72\\",\\"displayName\\": \\"Thumbnail (Lower Resolution)\\",\\"isMandatory\\": false,\\"validationActionEnabled\\": false},\\"updated\\": {\\"$numberLong\\": \\"1724391781131\\"}}},\\"playInfo\\": {\\"streamUrls\\": [{\\"url\\": \\"https://ultra-streams-msp.airtelxstream.in/streams/ULTRA/MOVIE/66c81a39795755238d2e8537/hls/playlist_verimatrix.m3u8\\",\\"streamType\\": \\"HLS\\",\\"drmType\\": \\"FAIRPLAY\\",\\"drmVendor\\": \\"VERIMATRIX\\",\\"dvr\\": false,\\"cdnName\\": \\"Google Media CDN\\"},{\\"url\\": \\"https://ultra-streams-msp.airtelxstream.in/streams/ULTRA/MOVIE/66c81a39795755238d2e8537/dash/manifest.mpd\\",\\"streamType\\": \\"DASH\\",\\"drmType\\": \\"WIDEVINE\\",\\"drmVendor\\": \\"VERIMATRIX\\",\\"dvr\\": false,\\"cdnName\\": \\"Google Media CDN\\"},{\\"url\\": \\"https://ultra-streams-msp.airtelxstream.in/streams/ULTRA/MOVIE/66c81a39795755238d2e8537/dash/manifest.mpd\\",\\"streamType\\": \\"DASH\\",\\"drmType\\": \\"PLAYREADY\\",\\"drmVendor\\": \\"VERIMATRIX\\",\\"dvr\\": false,\\"cdnName\\": \\"Google Media CDN\\"}]},\\"tempDeDupId\\": \\"sarfarosh:1999\\",\\"contentActionDetails\\": {\\"actionRequired\\": false,\\"contentActionData\\": {}},\\"filterAndSortFields\\": {\\"releaseDate\\": {\\"$numberLong\\": \\"925410600\\"}},\\"info\\": [{\\"data\\": \\" Metadata status updated \\",\\"ts\\": {\\"$numberLong\\": \\"1726207060648\\"}},{\\"data\\": \\" Metadata status updated \\",\\"ts\\": {\\"$numberLong\\": \\"1725530948054\\"}}],\\"archivedMetadata\\": {\\"skipCredits\\": null,\\"skipIntro\\": null},\\"highPriorityUpdate\\": 2,\\"drmInfo\\": {\\"nagraCkmResponse\\": {\\"PLAYREADY\\": {\\"status\\": \\"OK\\",\\"contentKey\\": {\\"keyId\\": \\"40ba8cbd-c3d7-446b-b2a0-9ae982d39344\\",\\"key\\": \\"mNB5FqSH8mMeW5VRJQ8uKQ==\\",\\"iv\\": \\"RK2gUMdKRCV0DgDRPIlNnw==\\"},\\"drmSignalization\\": {\\"dash\\": {\\"drmSystemId\\": \\"9a04f079-9840-4286-ab92-e65be0885f95\\",\\"drmName\\": \\"PLAYREADY\\",\\"manifestHeader\\": \\"<ContentProtection value=\\\\\\"MSPR 2.0\\\\\\" schemeIdUri=\\\\\\"urn:uuid:9a04f079-9840-4286-ab92-e65be0885f95\\\\\\"><mspr:pro xmlns:mspr=\\\\\\"urn:microsoft:playready\\\\\\">yAMAAAEAAQC+AzwAVwBSAE0ASABFAEEARABFAFIAIAB4AG0AbABuAHMAPQAiAGgAdAB0AHAAOgAvAC8AcwBjAGgAZQBtAGEAcwAuAG0AaQBjAHIAbwBzAG8AZgB0AC4AYwBvAG0ALwBEAFIATQAvADIAMAAwADcALwAwADMALwBQAGwAYQB5AFIAZQBhAGQAeQBIAGUAYQBkAGUAcgAiACAAdgBlAHIAcwBpAG8AbgA9ACIANAAuADAALgAwAC4AMAAiAD4APABEAEEAVABBAD4APABQAFIATwBUAEUAQwBUAEkATgBGAE8APgA8AEsARQBZAEwARQBOAD4AMQA2ADwALwBLAEUAWQBMAEUATgA+ADwAQQBMAEcASQBEAD4AQQBFAFMAQwBUAFIAPAAvAEEATABHAEkARAA+ADwALwBQAFIATwBUAEUAQwBUAEkATgBGAE8APgA8AEsASQBEAD4AdgBZAHkANgBRAE4AZgBEAGEAMABTAHkAbwBKAHIAcABnAHQATwBUAFIAQQA9AD0APAAvAEsASQBEAD4APABDAEgARQBDAEsAUwBVAE0APgAzAEIAeQA4AEcASQBTAEEAMQBOAHMAPQA8AC8AQwBIAEUAQwBLAFMAVQBNAD4APABMAEEAXwBVAFIATAA+AGgAdAB0AHAAcwA6AC8ALwBhAGkAcgA5AHEAMgBkAG4ALgBhAG4AeQBjAGEAcwB0AC4AbgBhAGcAcgBhAC4AYwBvAG0ALwBBAEkAUgA5AFEAMgBEAE4ALwBwAHIAbABzAC8AYwBvAG4AdABlAG4AdABsAGkAYwBlAG4AcwBlAHMAZQByAHYAaQBjAGUALwB2ADEALwBsAGkAYwBlAG4AcwBlAHMAPAAvAEwAQQBfAFUAUgBMAD4APABDAFUAUwBUAE8ATQBBAFQAVABSAEkAQgBVAFQARQBTAD4APABuAHYAOgBDAG8AbgB0AGUAbgB0AEkAZAAgAHgAbQBsAG4AcwA6AG4AdgA9ACIAdQByAG4AOgBzAGMAaABlAG0AYQAtAHMAcwBwAC0AbgBhAGcAcgBhAC0AYwBvAG0AIgA+ADYANgBjADgAMQBhADMAOQA3ADkANQA3ADUANQAyADMAOABkADIAZQA4ADUAMwA3ADwALwBuAHYAOgBDAG8AbgB0AGUAbgB0AEkAZAA+ADwALwBDAFUAUwBUAE8ATQBBAFQAVABSAEkAQgBVAFQARQBTAD4APAAvAEQAQQBUAEEAPgA8AC8AVwBSAE0ASABFAEEARABFAFIAPgA=</mspr:pro><cenc:pssh  xmlns:cenc=\\\\\\"urn:mpeg:cenc:2013\\\\\\">AAAD6HBzc2gAAAAAmgTweZhAQoarkuZb4IhflQAAA8jIAwAAAQABAL4DPABXAFIATQBIAEUAQQBEAEUAUgAgAHgAbQBsAG4AcwA9ACIAaAB0AHQAcAA6AC8ALwBzAGMAaABlAG0AYQBzAC4AbQBpAGMAcgBvAHMAbwBmAHQALgBjAG8AbQAvAEQAUgBNAC8AMgAwADAANwAvADAAMwAvAFAAbABhAHkAUgBlAGEAZAB5AEgAZQBhAGQAZQByACIAIAB2AGUAcgBzAGkAbwBuAD0AIgA0AC4AMAAuADAALgAwACIAPgA8AEQAQQBUAEEAPgA8AFAAUgBPAFQARQBDAFQASQBOAEYATwA+ADwASwBFAFkATABFAE4APgAxADYAPAAvAEsARQBZAEwARQBOAD4APABBAEwARwBJAEQAPgBBAEUAUwBDAFQAUgA8AC8AQQBMAEcASQBEAD4APAAvAFAAUgBPAFQARQBDAFQASQBOAEYATwA+ADwASwBJAEQAPgB2AFkAeQA2AFEATgBmAEQAYQAwAFMAeQBvAEoAcgBwAGcAdABPAFQAUgBBAD0APQA8AC8ASwBJAEQAPgA8AEMASABFAEMASwBTAFUATQA+ADMAQgB5ADgARwBJAFMAQQAxAE4AcwA9ADwALwBDAEgARQBDAEsAUwBVAE0APgA8AEwAQQBfAFUAUgBMAD4AaAB0AHQAcABzADoALwAvAGEAaQByADkAcQAyAGQAbgAuAGEAbgB5AGMAYQBzAHQALgBuAGEAZwByAGEALgBjAG8AbQAvAEEASQBSADkAUQAyAEQATgAvAHAAcgBsAHMALwBjAG8AbgB0AGUAbgB0AGwAaQBjAGUAbgBzAGUAcwBlAHIAdgBpAGMAZQAvAHYAMQAvAGwAaQBjAGUAbgBzAGUAcwA8AC8ATABBAF8AVQBSAEwAPgA8AEMAVQBTAFQATwBNAEEAVABUAFIASQBCAFUAVABFAFMAPgA8AG4AdgA6AEMAbwBuAHQAZQBuAHQASQBkACAAeABtAGwAbgBzADoAbgB2AD0AIgB1AHIAbgA6AHMAYwBoAGUAbQBhAC0AcwBzAHAALQBuAGEAZwByAGEALQBjAG8AbQAiAD4ANgA2AGMAOAAxAGEAMwA5ADcAOQA1ADcANQA1ADIAMwA4AGQAMgBlADgANQAzADcAPAAvAG4AdgA6AEMAbwBuAHQAZQBuAHQASQBkAD4APAAvAEMAVQBTAFQATwBNAEEAVABUAFIASQBCAFUAVABFAFMAPgA8AC8ARABBAFQAQQA+ADwALwBXAFIATQBIAEUAQQBEAEUAUgA+AA==</cenc:pssh></ContentProtection>\\",\\"psshBox\\": \\"AAAD6HBzc2gAAAAAmgTweZhAQoarkuZb4IhflQAAA8jIAwAAAQABAL4DPABXAFIATQBIAEUAQQBEAEUAUgAgAHgAbQBsAG4AcwA9ACIAaAB0AHQAcAA6AC8ALwBzAGMAaABlAG0AYQBzAC4AbQBpAGMAcgBvAHMAbwBmAHQALgBjAG8AbQAvAEQAUgBNAC8AMgAwADAANwAvADAAMwAvAFAAbABhAHkAUgBlAGEAZAB5AEgAZQBhAGQAZQByACIAIAB2AGUAcgBzAGkAbwBuAD0AIgA0AC4AMAAuADAALgAwACIAPgA8AEQAQQBUAEEAPgA8AFAAUgBPAFQARQBDAFQASQBOAEYATwA+ADwASwBFAFkATABFAE4APgAxADYAPAAvAEsARQBZAEwARQBOAD4APABBAEwARwBJAEQAPgBBAEUAUwBDAFQAUgA8AC8AQQBMAEcASQBEAD4APAAvAFAAUgBPAFQARQBDAFQASQBOAEYATwA+ADwASwBJAEQAPgB2AFkAeQA2AFEATgBmAEQAYQAwAFMAeQBvAEoAcgBwAGcAdABPAFQAUgBBAD0APQA8AC8ASwBJAEQAPgA8AEMASABFAEMASwBTAFUATQA+ADMAQgB5ADgARwBJAFMAQQAxAE4AcwA9ADwALwBDAEgARQBDAEsAUwBVAE0APgA8AEwAQQBfAFUAUgBMAD4AaAB0AHQAcABzADoALwAvAGEAaQByADkAcQAyAGQAbgAuAGEAbgB5AGMAYQBzAHQALgBuAGEAZwByAGEALgBjAG8AbQAvAEEASQBSADkAUQAyAEQATgAvAHAAcgBsAHMALwBjAG8AbgB0AGUAbgB0AGwAaQBjAGUAbgBzAGUAcwBlAHIAdgBpAGMAZQAvAHYAMQAvAGwAaQBjAGUAbgBzAGUAcwA8AC8ATABBAF8AVQBSAEwAPgA8AEMAVQBTAFQATwBNAEEAVABUAFIASQBCAFUAVABFAFMAPgA8AG4AdgA6AEMAbwBuAHQAZQBuAHQASQBkACAAeABtAGwAbgBzADoAbgB2AD0AIgB1AHIAbgA6AHMAYwBoAGUAbQBhAC0AcwBzAHAALQBuAGEAZwByAGEALQBjAG8AbQAiAD4ANgA2AGMAOAAxAGEAMwA5ADcAOQA1ADcANQA1ADIAMwA4AGQAMgBlADgANQAzADcAPAAvAG4AdgA6AEMAbwBuAHQAZQBuAHQASQBkAD4APAAvAEMAVQBTAFQATwBNAEEAVABUAFIASQBCAFUAVABFAFMAPgA8AC8ARABBAFQAQQA+ADwALwBXAFIATQBIAEUAQQBEAEUAUgA+AA==\\"}},\\"_class\\": \\"in.wynk.msp.service.transcode.shakapackager.drm.dto.NagraCkmResponse\\"},\\"WIDEVINE\\": {\\"status\\": \\"OK\\",\\"contentKey\\": {\\"keyId\\": \\"40ba8cbd-c3d7-446b-b2a0-9ae982d39344\\",\\"key\\": \\"mNB5FqSH8mMeW5VRJQ8uKQ==\\",\\"iv\\": \\"RK2gUMdKRCV0DgDRPIlNnw==\\"},\\"drmSignalization\\": {\\"dash\\": {\\"drmSystemId\\": \\"edef8ba9-79d6-4ace-a3c8-27dcd51d21ed\\",\\"drmName\\": \\"WideVine\\",\\"manifestHeader\\": \\"<ContentProtection schemeIdUri=\\\\\\"urn:uuid:edef8ba9-79d6-4ace-a3c8-27dcd51d21ed\\\\\\"><cenc:pssh xmlns:cenc=\\\\\\"urn:mpeg:cenc:2013\\\\\\">AAAATnBzc2gAAAAA7e+LqXnWSs6jyCfc1R0h7QAAAC4IARIQQLqMvcPXRGuyoJrpgtOTRCIYNjZjODFhMzk3OTU3NTUyMzhkMmU4NTM3</cenc:pssh></ContentProtection>\\",\\"psshBox\\": \\"AAAATnBzc2gAAAAA7e+LqXnWSs6jyCfc1R0h7QAAAC4IARIQQLqMvcPXRGuyoJrpgtOTRCIYNjZjODFhMzk3OTU3NTUyMzhkMmU4NTM3\\"}},\\"_class\\": \\"in.wynk.msp.service.transcode.shakapackager.drm.dto.NagraCkmResponse\\"},\\"FAIRPLAY\\": {\\"status\\": \\"OK\\",\\"contentKey\\": {\\"keyId\\": \\"1a390d2e-9567-468a-b1de-fc01a5c14019\\",\\"key\\": \\"5cDBoSwEbtVsl8kyTN0g/w==\\",\\"iv\\": \\"tl5aFK2DAf55r0g/BF7Vlg==\\"},\\"drmSignalization\\": {\\"hls\\": {\\"drmSystemId\\": \\"94ce86fb-07ff-4f43-adb8-93d2fa968ca2\\",\\"drmName\\": \\"FairPlay\\",\\"keyUri\\": \\"skd://eyJDb250ZW50SWQiOiI2NmM4MWEzOTc5NTc1NTIzOGQyZTg1MzciLCJLZXlJZCI6IjFhMzkwZDJlLTk1NjctNDY4YS1iMWRlLWZjMDFhNWMxNDAxOSIsIklWIjoidGw1YUZLMkRBZjU1cjBnL0JGN1ZsZz09In0=\\"}},\\"_class\\": \\"in.wynk.msp.service.transcode.shakapackager.drm.dto.NagraCkmResponse\\"}}},\\"tx\\": 22}",
                		"updateDescription": {
                			"removedFields": null,
                			"updatedFields": "{\\"metadata.tx\\": 25}",
                			"truncatedArrays": null
                		},
                		"source": {
                			"version": "2.3.2.Final",
                			"connector": "mongodb",
                			"name": "msp",
                			"ts_ms": 1769009960000,
                			"snapshot": "false",
                			"db": "msp",
                			"sequence": null,
                			"rs": "msp",
                			"collection": "content",
                			"ord": 2,
                			"lsid": null,
                			"txnNumber": null,
                			"wallTime": 1769009960296
                		},
                		"op": "u",
                		"ts_ms": 1769009960301,
                		"transaction": null
                	}
                }
                """;

        // Kafka Producer properties with security
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put("security.protocol", "SASL_PLAINTEXT");
        properties.put("sasl.mechanism", "PLAIN");
        properties.put("sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required " +
                "username=\"" + username + "\" " +
                "password=\"" + password + "\";");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(properties)) {
            ProducerRecord<String, String> record = new ProducerRecord<>(topicName, key, value);
            RecordMetadata metadata = producer.send(record).get(); // Synchronous send

            System.out.printf("Message sent to topic %s partition %d offset %d%n",
                    metadata.topic(), metadata.partition(), metadata.offset());

        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error sending message to Kafka: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}
