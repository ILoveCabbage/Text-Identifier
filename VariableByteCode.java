import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class VariableByteCode{
	static int mask8bit = (1 << 8) - 1;
	
	public static void innerEncode(int num, List<Byte> resultList) {
		int headNum = resultList.size();
		while (true) {
			byte n = (byte) (num % 128);
			resultList.add(headNum, n);
			if (num < 128)
				break;
			num = num >>> 7;
		}

		int lastIndex = resultList.size() - 1;
		Byte val = resultList.get(lastIndex);
		val = (byte) (val.byteValue() - 128);
		resultList.remove(lastIndex);
		resultList.add(val);
	}

	/**
	 * Method for Var-Byte encoding
	 * 
	 * @param list
	 * @return
	 */
	public static byte[] encode(List<Integer> list) {
		List<Byte> resultList = new ArrayList<Byte>();
		for (Integer num:list) {
			innerEncode(num.intValue() , resultList);
		}
		int listNum = resultList.size();
		byte[] resultArray = new byte[listNum + 4];
		int num = list.size();

		resultArray[0] = (byte) ((num >> 24) & mask8bit);
		resultArray[1] = (byte) ((num >> 16) & mask8bit);
		resultArray[2] = (byte) ((num >> 8) & mask8bit);
		resultArray[3] = (byte) (num & mask8bit);

		for (int i = 0; i < listNum; i++)
			resultArray[i + 4] = resultList.get(i);

		return resultArray;
	}

	/**
	 * Method for Var-Byte decoding
	 * 
	 * @param encodedArray
	 * @return
	 */
	public static List<Integer> decode(byte[] encodedArray) {
		ArrayList<Integer> decodedArray = new ArrayList<Integer>();
		int n = 0;
		for (int i = 4; i < encodedArray.length; i++) {

			if (0 <= encodedArray[i])
				n = (n << 7) + encodedArray[i];
			else {
				n = (n << 7) + (encodedArray[i] + 128);
				decodedArray.add(n);
				n = 0;
			}
		}
		return decodedArray;

	}
	
	/*
	 * interpolate encode
	 */
	public static byte[] encodeInterpolate(List<Integer> numbers) {
        int last = -1;
        List<Integer> interpolateNumber = new ArrayList<Integer>();
        for (int i = 0; i < numbers.size(); i++) {
            Integer num = numbers.get(i);
            if (i == 0) {
                interpolateNumber.add(num);
            } else {
                interpolateNumber.add(num - last);
            }
            last = num;
        }

        byte[] rv = encode(interpolateNumber);
        return rv;
    }

	/*
	 * interpolate decode
	 */
	public static List<Integer> decodeInterpolate(byte[] encodedArray){
		List<Integer> uninterpolate = decode(encodedArray);
		List<Integer> rv = new ArrayList<Integer>(); 
		int last = -1;
		for (int i =0 ; i < uninterpolate.size(); i++) {
			Integer num = uninterpolate.get(i);
			if ( i ==0 ){
				rv.add(num);
			} else {
				num = num + last;
				rv.add(num);
			}
			last = num;
		}
		return rv;
	}
	
}
