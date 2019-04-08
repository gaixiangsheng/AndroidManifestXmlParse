package erlin.xml;

import java.util.ArrayList;

public class AndroidManifestParse {
    private static final int STRING_CHUNK_BASE_POSITION = 8;
    private static ArrayList<String> stringChunkContent = new ArrayList<>();

    public static void main(String[] args) {
        byte[] manifestBytes = Utils.readAndroidManifestToByteArray("./AndroidManifest.xml");
        printFileHeader(manifestBytes);
        parseStringChunk(manifestBytes);
        parseResourceChunk(manifestBytes);
        parseStartNamespaceChunk(manifestBytes);
        parseStartTagChunk1(manifestBytes);
    }


    public static void parseStartTagChunk1(byte[] bytes) {
        if (!Utils.checkBytes(bytes)) {
            return;
        }

        int startTagChunkOffsets = STRING_CHUNK_BASE_POSITION + 0x00000504 + 0x00000040 + 0x00000018;
        byte[] startTagThunkBytes = Utils.copyBytes(bytes, startTagChunkOffsets, 4);
        byte[] startTagThunkSizeBytes = Utils.copyBytes(bytes, startTagChunkOffsets + 4, 4);

        System.out.println("startTagThunkBytes 0x:" + Utils.bytes2HexString(startTagThunkBytes));
        System.out.println("startTagThunkSizeBytes 0x:" + Utils.bytes2HexString(startTagThunkSizeBytes) + " , int :" + Utils.bytes2Int(startTagThunkSizeBytes));

        byte[] startTagThunkContentBytes = Utils.copyBytes(bytes, startTagChunkOffsets + STRING_CHUNK_BASE_POSITION, Utils.bytes2Int(startTagThunkSizeBytes));
        byte[] lineNubmerBytes = Utils.copyBytes(startTagThunkContentBytes, 0, 4);
        System.out.println("lineNubmer 0x:" + Utils.bytes2HexString(lineNubmerBytes) + " , int : " + Utils.bytes2Int(lineNubmerBytes));

        byte[] namespaceBytes = Utils.copyBytes(startTagThunkContentBytes, 8, 4);
        System.out.println("namespace 0x:" + Utils.bytes2HexString(namespaceBytes) + " , int : " + Utils.bytes2Int(namespaceBytes) + " , url : " + getStringChunkContent(Utils.bytes2Int(namespaceBytes)));

        byte[] name = Utils.copyBytes(startTagThunkContentBytes, 12, 4);
        System.out.println("name 0x:" + Utils.bytes2HexString(name) + " , int : " + Utils.bytes2Int(name) + " , string : " + getStringChunkContent(Utils.bytes2Int(name)));

        byte[] flags = Utils.copyBytes(startTagThunkContentBytes, 16, 4);
        System.out.println("flags 0x:" + Utils.bytes2HexString(flags) + " , int : " + Utils.bytes2Int(flags));

        byte[] attr = Utils.copyBytes(startTagThunkContentBytes, 20, 4);
        int attrCount = Utils.bytes2Int(attr);
        System.out.println("attr 0x:" + Utils.bytes2HexString(attr) + " , attr count : " + attrCount);

        byte[] claAttr = Utils.copyBytes(startTagThunkContentBytes, 24, 4);
        System.out.println("class attr 0x:" + Utils.bytes2HexString(claAttr) + " , class attr count : " + Utils.bytes2Int(claAttr));

        byte[] attributesContentBytes = Utils.copyBytes(startTagThunkContentBytes, 28, attrCount * 5 * 4);
        System.out.println("attributesContentBytes len : " + attributesContentBytes.length);
        ArrayList<AttributeData> attrs = new ArrayList<>();
        for (int i = 0; i < attrCount; i++) {
            AttributeData attrData = new AttributeData();
            for (int j = 0; j < 5; j++) {
                int index = Utils.bytes2Int(Utils.copyBytes(attributesContentBytes, i * 5 * 4 + j * 4, 4));
                switch (index) {
                    case 0://namespaceuri
                        attrData.nameSpaceUri = index;
                        break;
                    case 1://name
                        attrData.name = index;
                        break;
                    case 2://value string
                        attrData.valuestring = index;
                        break;
                    case 3://type
                        attrData.type = index >> 24;
                        break;
                    case 4://data
                        attrData.data = index;
                        break;
                }
            }
            attrs.add(attrData);
        }
        System.out.println("==============");
        for (int i = 0; i < attrCount; i++) {
            AttributeData aData = attrs.get(i);
            System.out.println("namespaceuri = "+getStringChunkContent(aData.nameSpaceUri));
            System.out.println("name = "+getStringChunkContent(aData.name));
            System.out.println("valuestring = "+getStringChunkContent(aData.valuestring));
            System.out.println("type = "+(aData.type == -1 ?"null":AttributeType.getAttrType(aData.type)));
            System.out.println("data = "+(aData.data == -1 ?"null":AttributeType.getAttributeData(aData)));
            System.out.println("==============");
        }
    }



//    public static void parseStartTagChunk(byte[] bytes) {
//        if (!Utils.checkBytes(bytes)) {
//            return;
//        }
//
//        int startTagChunkOffsets = STRING_CHUNK_BASE_POSITION + 0x00000504 + 0x00000040 + 0x00000018;
//        byte[] startTagThunkBytes = Utils.copyBytes(bytes, startTagChunkOffsets, 4);
//        byte[] startTagThunkSizeBytes = Utils.copyBytes(bytes, startTagChunkOffsets + 4, 4);
//        byte[] lineNumberBytes = Utils.copyBytes(bytes, startTagChunkOffsets + 8, 4);
//        byte[] namespaceUriBytes = Utils.copyBytes(bytes, startTagChunkOffsets + 16, 4);
//        byte[] nameBytes = Utils.copyBytes(bytes, startTagChunkOffsets + 20, 4);
//        byte[] flagsBytes = Utils.copyBytes(bytes, startTagChunkOffsets + 24, 4);
//        byte[] attributeCountBytes = Utils.copyBytes(bytes, startTagChunkOffsets + 28, 4);
//        byte[] classAttributeBytes = Utils.copyBytes(bytes, startTagChunkOffsets + 32, 4);
//
//        int attributeCount = Utils.bytes2Int(attributeCountBytes);
//        byte[] attributesBytes = Utils.copyBytes(bytes, startTagChunkOffsets + 36, attributeCount * 5 * 4);
//
//        System.out.println("startTagThunkBytes 0x:" + Utils.bytes2HexString(startTagThunkBytes));
//        System.out.println("startTagThunkSizeBytes 0x:" + Utils.bytes2HexString(startTagThunkSizeBytes) + " , int :" + Utils.bytes2Int(startTagThunkSizeBytes));
//        System.out.println("lineNumberBytes 0x:" + Utils.bytes2HexString(startTagThunkSizeBytes) + " , line number int :" + Utils.bytes2Int(lineNumberBytes));
//        System.out.println("namespaceUriBytes 0x:" + Utils.bytes2HexString(namespaceUriBytes) + " , namespace index int :" + Utils.bytes2Int(namespaceUriBytes) + " , namespaceUri :" + getStringChunkContent(Utils.bytes2Int(namespaceUriBytes)));
//        System.out.println("nameBytes 0x:" + Utils.bytes2HexString(nameBytes) + " , name index int :" + Utils.bytes2Int(nameBytes) + " , name string :" + getStringChunkContent(Utils.bytes2Int(nameBytes)));
//        System.out.println("flagsBytes 0x:" + Utils.bytes2HexString(flagsBytes) + " , flags index int :" + Utils.bytes2Int(flagsBytes) + " , flags string :" + getStringChunkContent(Utils.bytes2Int(flagsBytes)));
//        System.out.println("attributeCountBytes 0x:" + Utils.bytes2HexString(attributeCountBytes) + " , attribute Count :" + Utils.bytes2Int(attributeCountBytes));
//        System.out.println("classAttributeBytes 0x:" + Utils.bytes2HexString(classAttributeBytes) + " , classAttribute index int :" + Utils.bytes2Int(classAttributeBytes) + " , classAttribute string :" + getStringChunkContent(Utils.bytes2Int(classAttributeBytes)));
//
//        ArrayList<AttributeData> attrs = new ArrayList<>();
//        for (int i = 0; i < attributeCount; i++) {
//            for (int j = 0; j < 5; j++) {
//                int value = Utils.bytes2Int(Utils.copyBytes(attributesBytes, i * 20 + j * 4, 4));
//                AttributeData attr = new AttributeData();
//                switch (value) {
//                    case 0:
//                        attr.nameSpaceUri = value;
//                        break;
//                    case 1:
//                        attr.name = value;
//                        break;
//                    case 2:
//                        attr.valuestring = value;
//                        break;
//                    case 3:
//                        attr.type = (value >> 24);
//                        break;
//                    case 4:
//                        attr.data = value;
//                        break;
//                }
//                attrs.add(attr);
//            }
//        }
//
//        for (int i = 0; i < attributeCount; i++) {
//            if (attrs.get(i).nameSpaceUri != -1) {
//                System.out.print("nameSpaceUri = " + stringChunkContent.get(attrs.get(i).nameSpaceUri) + " ，");
//            } else {
//                System.out.print("nameSpaceUri is null ，");
//            }
//
//            if (attrs.get(i).name != -1) {
//                System.out.print("name = " + stringChunkContent.get(attrs.get(i).name) + " ，");
//            } else {
//                System.out.print("name is null ，");
//            }
//
//            if (attrs.get(i).valuestring != -1) {
//                System.out.print("valuestring = " + stringChunkContent.get(attrs.get(i).valuestring) + " ，");
//            } else {
//                System.out.print("valuestring is null ，");
//            }
//
//            if (attrs.get(i).type != -1) {
//                System.out.print("type = " + AttributeType.getAttrType(attrs.get(i).type) + " ，");
//            } else {
//                System.out.print("type is null ，");
//            }
//
//            if (attrs.get(i).data != -1) {
//                System.out.println("data = " + AttributeType.getAttrType(attrs.get(i).data));
//            } else {
//                System.out.println("data is null ");
//            }
//        }
//    }

    public static String getStringChunkContent(int index) {
        if (index < 0 || index > stringChunkContent.size()) {
            return "null";
        }
        return stringChunkContent.get(index);
    }


    public static void parseStartNamespaceChunk(byte[] bytes) {
        if (!Utils.checkBytes(bytes)) {
            return;
        }

        int startNamespaceChunkOffsets = STRING_CHUNK_BASE_POSITION + 0x00000504 + 0x00000040;
        byte[] startNamespaceChunkBytes = Utils.copyBytes(bytes, startNamespaceChunkOffsets, 4);
        byte[] startNamespaceChunkSizeBytes = Utils.copyBytes(bytes, startNamespaceChunkOffsets + 4, 4);
        byte[] lineNumberBytes = Utils.copyBytes(bytes, startNamespaceChunkOffsets + 8, 4);
        byte[] unknownBytes = Utils.copyBytes(bytes, startNamespaceChunkOffsets + 12, 4);
        byte[] prefixBytes = Utils.copyBytes(bytes, startNamespaceChunkOffsets + 16, 4);
        byte[] uriBytes = Utils.copyBytes(bytes, startNamespaceChunkOffsets + 20, 4);

        System.out.println("startNamespaceChunk 0x:" + Utils.bytes2HexString(startNamespaceChunkBytes));
        System.out.println("startNamespaceChunkSize 0x:" + Utils.bytes2HexString(startNamespaceChunkBytes) + " , int :" + Utils.bytes2Int(startNamespaceChunkSizeBytes));
        System.out.println("lineNumberBytes 0x:" + Utils.bytes2HexString(lineNumberBytes) + " , int :" + Utils.bytes2Int(lineNumberBytes));
        System.out.println("unknownBytes 0x:" + Utils.bytes2HexString(unknownBytes) + " , int :" + Utils.bytes2Int(unknownBytes));
        System.out.println("prefixBytes 0x:" + Utils.bytes2HexString(prefixBytes) + " , string chunk index :" + Utils.bytes2Int(prefixBytes) + " , prefix string :" + stringChunkContent.get(Utils.bytes2Int(prefixBytes)));
        System.out.println("uriBytes 0x:" + Utils.bytes2HexString(uriBytes) + " , string chunk index :" + Utils.bytes2Int(uriBytes) + " , uri string :" + stringChunkContent.get(Utils.bytes2Int(uriBytes)));


    }


    public static void parseResourceChunk(byte[] bytes) {
        if (!Utils.checkBytes(bytes)) {
            return;
        }

        //取出ResourceChunk中所有的内容体
        int resourceOffsets = STRING_CHUNK_BASE_POSITION + 0x00000504;
        byte[] resourceChunkBytes = Utils.copyBytes(bytes, resourceOffsets, 4);
        byte[] resourceChunkSizeBytes = Utils.copyBytes(bytes, resourceOffsets + 4, 4);
        byte[] resourceIdsBytes = Utils.copyBytes(bytes, resourceOffsets + 8, 4);

        System.out.println("ResourceChunkType:0x" + Utils.bytes2HexString(resourceChunkBytes));
        System.out.println("ResourceChunkSize:0x" + Utils.bytes2HexString(resourceChunkSizeBytes) + " ,int :" + Utils.bytes2Int(resourceChunkSizeBytes));
        System.out.println("ResourceIds:0x" + Utils.bytes2HexString(resourceIdsBytes));

        byte[] resourceIdsContentBytes = Utils.copyBytes(bytes, resourceOffsets + STRING_CHUNK_BASE_POSITION, Utils.bytes2Int(resourceChunkSizeBytes) - STRING_CHUNK_BASE_POSITION);
        int resouceIdCount = resourceIdsContentBytes.length / 4;
        System.out.println("Resource id Size : " + resouceIdCount);
        ArrayList<Integer> resourceIdList = new ArrayList<>(resouceIdCount);
        int index = 1;
        for (int i = 0; i < resourceIdsContentBytes.length; i += 4) {
            int resId = Utils.bytes2Int(Utils.copyBytes(resourceIdsContentBytes, i, 4));
            System.out.println((index++) + " ,resource id:" + resId + " ,hex: 0x" + Utils.bytes2HexString(Utils.copyBytes(resourceIdsContentBytes, i, 4)));
            resourceIdList.add(resId);
        }
    }

    public static void parseStringChunk(byte[] bytes) {
        if (!Utils.checkBytes(bytes)) {
            return;
        }

        // 取出StringChunk中所有的内容体
        byte[] stringSizeBytes = Utils.copyBytes(bytes, STRING_CHUNK_BASE_POSITION + 4, 4);
        byte[] stringCountBytes = Utils.copyBytes(bytes, STRING_CHUNK_BASE_POSITION + 8, 4);
        byte[] stringPoolOffsetBytes = Utils.copyBytes(bytes, STRING_CHUNK_BASE_POSITION + 20, 4);

        //1. 偏移到StringChunk字符池内容开始位置： STRING_CHUNK_BASE_POSITION + stringPoolOffsetBytes字符池的偏移值
        int start = STRING_CHUNK_BASE_POSITION + Utils.bytes2Int(stringPoolOffsetBytes);
        //2. StringChunk字符池结束位置：stringSizeBytes
        int end = Utils.bytes2Int(stringSizeBytes);
        //3. 读取start开始位置到结束位置，即为字符池的全部内容
        byte[] stringChunkContentBytes = Utils.copyBytes(bytes, start, end);

        int firstStringSize = Utils.bytes2Short(Utils.copyBytes(stringChunkContentBytes, 0, 2)) * 2;//一个字符占两个字节，所以*2
        stringChunkContent.add(new String(Utils.filterInvalidBytes(Utils.copyBytes(stringChunkContentBytes, 2, firstStringSize))));//跳过字符长度的两个字节，取出字符串。
        //System.out.println(stringChunkContent.get(stringChunkContent.size() - 1));//打印

        int stringCount = Utils.bytes2Int(stringCountBytes);//计算字符串池中一共有多少个字符串

        firstStringSize += 2 + 2;//每一个字符串+跳过字符长度的两个字节+跳过字符00 00结尾的字节，等于下一字符串开始的位置
        while (stringChunkContent.size() < stringCount) {
            int stringSize = Utils.bytes2Short(Utils.copyBytes(stringChunkContentBytes, firstStringSize, 2)) * 2;//计算下一个字符串长度
            stringChunkContent.add(new String(Utils.filterInvalidBytes(Utils.copyBytes(stringChunkContentBytes, firstStringSize + 2, stringSize))));//提取字符串
            firstStringSize += 2 + stringSize + 2;//计算下一个字符串开始位置
            //System.out.println(stringChunkContent.get(stringChunkContent.size() - 1));
        }
    }

//    public static void parseStringChunkContent(byte[] bytes){
//        if (!Utils.checkBytes(bytes)) {
//            return;
//        }
//
//        // 取出StringChunk中所有的内容体
//        byte[] stringSizeBytes = Utils.copyBytes(bytes,STRING_CHUNK_BASE_POSITION+4,4);
//        byte[] stringCountBytes = Utils.copyBytes(bytes,STRING_CHUNK_BASE_POSITION+8,4);
//        byte[] stringPoolOffsetBytes = Utils.copyBytes(bytes,STRING_CHUNK_BASE_POSITION+20,4);
//
//
//        ArrayList<String> stringArrayList = new ArrayList<>();
//        //1. 偏移到StringChunk字符池内容开始位置： STRING_CHUNK_BASE_POSITION + stringPoolOffsetBytes字符池的偏移值
//        int start = STRING_CHUNK_BASE_POSITION+Utils.bytes2Int(stringPoolOffsetBytes);
//        //2. StringChunk字符池结束位置：stringSizeBytes
//        int end = Utils.bytes2Int(stringSizeBytes);
//        //3. 读取start开始位置到结束位置，即为字符池的全部内容
//        byte[] stringChunkContentBytes = Utils.copyBytes(bytes,start,end);
//
//        int firstStringSize = Utils.bytes2Short(Utils.copyBytes(stringChunkContentBytes,0,2))*2;//一个字符占两个字节，所以*2
//        stringArrayList.add(new String(Utils.filterInvalidBytes(Utils.copyBytes(stringChunkContentBytes,2,firstStringSize))));//跳过字符长度的两个字节，取出字符串。
//        System.out.println(stringArrayList.get(stringArrayList.size()-1));//打印
//
//        int stringCount = Utils.bytes2Int(stringCountBytes);//计算字符串池中一共有多少个字符串
//
//        firstStringSize+=2+2;//每一个字符串+跳过字符长度的两个字节+跳过字符00 00结尾的字节，等于下一字符串开始的位置
//        while (stringArrayList.size()<stringCount){
//            int stringSize = Utils.bytes2Short(Utils.copyBytes(stringChunkContentBytes,firstStringSize,2))*2;//计算下一个字符串长度
//            stringArrayList.add(new String(Utils.filterInvalidBytes(Utils.copyBytes(stringChunkContentBytes,firstStringSize+2,stringSize))));//提取字符串
//            firstStringSize+=2+stringSize+2;//计算下一个字符串开始位置
//            System.out.println(stringArrayList.get(stringArrayList.size()-1));
//        }
//    }


//    public static ArrayList<String> parseStringChunkContent(byte[] bytes) {
//        if (!Utils.checkBytes(bytes)) {
//            return null;
//        }
//
//        // 取出StringChunk中所有的内容体
//        byte[] stringSizeBytes = Utils.copyBytes(bytes, STRING_CHUNK_BASE_POSITION + 4, 4);
//        byte[] stringCountBytes = Utils.copyBytes(bytes, STRING_CHUNK_BASE_POSITION + 8, 4);
//        byte[] stringPoolOffsetBytes = Utils.copyBytes(bytes, STRING_CHUNK_BASE_POSITION + 20, 4);
//
//
//        ArrayList<String> stringArrayList = new ArrayList<>();
//        //1. 偏移到StringChunk字符池内容开始位置： STRING_CHUNK_BASE_POSITION + stringPoolOffsetBytes字符池的偏移值
//        int start = STRING_CHUNK_BASE_POSITION + Utils.bytes2Int(stringPoolOffsetBytes);
//        //2. StringChunk字符池结束位置：stringSizeBytes
//        int end = Utils.bytes2Int(stringSizeBytes);
//        //3. 读取start开始位置到结束位置，即为字符池的全部内容
//        byte[] stringChunkContentBytes = Utils.copyBytes(bytes, start, end);
//
//        int firstStringSize = Utils.bytes2Short(Utils.copyBytes(stringChunkContentBytes, 0, 2)) * 2;//一个字符占两个字节，所以*2
//        stringArrayList.add(new String(Utils.filterInvalidBytes(Utils.copyBytes(stringChunkContentBytes, 2, firstStringSize))));//跳过字符长度的两个字节，取出字符串。
//        //System.out.println(stringArrayList.get(stringArrayList.size()-1));//打印
//
//        int stringCount = Utils.bytes2Int(stringCountBytes);//计算字符串池中一共有多少个字符串
//
//        firstStringSize += 2 + 2;//每一个字符串+跳过字符长度的两个字节+跳过字符00 00结尾的字节，等于下一字符串开始的位置
//        while (stringArrayList.size() < stringCount) {
//            int stringSize = Utils.bytes2Short(Utils.copyBytes(stringChunkContentBytes, firstStringSize, 2)) * 2;//计算下一个字符串长度
//            stringArrayList.add(new String(Utils.filterInvalidBytes(Utils.copyBytes(stringChunkContentBytes, firstStringSize + 2, stringSize))));//提取字符串
//            firstStringSize += 2 + stringSize + 2;//计算下一个字符串开始位置
//            //System.out.println(stringArrayList.get(stringArrayList.size()-1));
//        }
//        return stringArrayList;
//    }


    public static void printFileHeader(byte[] bytes) {
        if (!Utils.checkBytes(bytes)) {
            return;
        }

        byte[] magicNumberBytes = Utils.copyBytes(bytes, 0, 4);
        byte[] fileSizeBytes = Utils.copyBytes(bytes, 4, 4);

        String magicNumberHex = Utils.bytes2HexString(magicNumberBytes);
        String fileSize = Utils.bytes2Int(fileSizeBytes) + " byte";
        System.out.println("MagicNumber :0x" + magicNumberHex);
        System.out.println("File Size   :" + fileSize);
    }
}
