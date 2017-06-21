import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class DataManager {

	/**
	 * 从books.txt中提取数据到程序中。
	 * */
	public void prepareRecords(ArrayList<BookInfoBean> list, String inputFile) {
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		list.clear();
		try {
			fis = new FileInputStream(inputFile);
			isr = new InputStreamReader(fis, "GB2312");
			br = new BufferedReader(isr);
			
			do{
				String line = br.readLine();
//				System.out.println(line);
				//判断是否读取完毕。
				if(line == null || "".equals(line)){
					System.out.println("read records end!");
					break;
				}
				list.add(pickBookInfo(line));
			}while(true);
			
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Prepare records finish!");
	}
	
	/**
	 * 与prepareRecords方法配合使用。
	 * 将读取进来的一行数据解析成单条的图书数据。
	 * */
	private BookInfoBean pickBookInfo(String line){
		BookInfoBean bean = new BookInfoBean();
		System.out.println("line:"+line);
		String tmp[] = line.split("\t");
		//检查错误数据这块很复杂。要考虑的问题很多。
		if(tmp.length != 4){
			bean.setType(1);
			bean.setSerial(1);
			bean.setName(null);
			bean.setIsbn(line);
		}else{
			try{
				//类型
				bean.setType(Integer.valueOf(tmp[0]));
				//序列号
				bean.setSerial(Integer.valueOf(tmp[1]));
				//名称
				bean.setName(tmp[2]);
				//ISBN
				bean.setIsbn(tmp[3]);
			}catch(NumberFormatException e){
				System.out.println("A record was format error!!!:"+line);
				bean.setType(1);
				bean.setSerial(1);
				bean.setName(null);
				bean.setIsbn(line);
			}//catch  --  end.
		}
		
		return bean;
	}//pickBookInfo  --  end.
	
	/**
	 * 给已读取的图书数据进行排序。
	 * */
	public void sort(ArrayList<BookInfoBean> list){
		for(int i = 0; i < list.size() - 1; i++){
			BookInfoBean bean = list.get(i);
			int min = i;
			//排序方式，我自己想出来的哦。
			for(int j = i+1; j < list.size(); j++){
				if(bean.getType() > list.get(j).getType()){
					bean = list.get(j);
					min = j;
				}else if(bean.getType() == list.get(j).getType()){
					//这里是当类型号相等时，根据序列号由大到小排序。
					if(bean.getSerial() < list.get(j).getSerial()){
						bean = list.get(j);
						min = j;
					}
				}
			}
			//Insert the min.
			if(min != i){
				list.remove(min);
				list.add(i, bean);
			}
		}
		System.out.println("\n\nsort finish..\n");
	}

	/**
	 * 根据规则检查数据是否有错误，有错误的就把它放到后面去。
	 * */
	public void check(ArrayList<BookInfoBean> list) {
		/*

		 1. “类型编号”的范围是1~255；
		 2. “类内序列号”的范围1~999；
		 3. “书名”长度小于64字节；
		 4. “条形码”长度18字节，其中前4字节固定为"ISBN"，后14字节为数字。
		 
		 */
		
		/**用临时保存错误数据记录。*/
		ArrayList<BookInfoBean> errorList = new ArrayList<>();
		
		System.out.println("Going to check data...");
		
		for(int i = 0; i < list.size(); i++){
			BookInfoBean bean = list.get(i);
			System.out.println("\n\ntype:"+bean.getType());
			//1.检查类型是否有错误。
			if(bean.getType() < 1 || bean.getType() > 255){
				errorList.add(bean);
				continue;
			}else if(bean.getType() == 1 && bean.getSerial() == 1 && bean.getName() == null){
				/*
				 * 对于有格式错误的数据，已经在这里处理掉了。后面的序列号、书名及ISBN都不需要再处理格式错误的问题。
				 * */
				errorList.add(bean);
				continue;
			}
			
			//2.检查序列号是否有错误。
			System.out.println("serial:"+bean.getSerial());
			if(bean.getSerial() < 1 || bean.getSerial() > 999){
				errorList.add(bean);
				continue;
			}
			
			//3.检查图书名称是否有错误。
			if(bean.getName() == null){
				System.out.println("Book name is null!");
				errorList.add(bean);
				continue;
			}else{
				System.out.println("name length:"+bean.getName().getBytes().length);
				if(bean.getName().getBytes().length > 64){
					errorList.add(bean);
					continue;
				}
			}
			
			//4.检查ISBN是否有错误。
			String isbn = bean.getIsbn();
			System.out.println("ISBN:"+isbn);
			boolean isISBNError = false;
			if(isbn == null){
				System.out.println("ISBN is null!");
				isISBNError = true;
			}else if(isbn.getBytes().length > 18){
				isISBNError = true;
			}else{
				if(!isbn.startsWith("ISBN")){//区分大小写
					isISBNError = true;
				}else{
					String last14Bytes = isbn.substring(4, isbn.length());
					System.out.println("isbnlast14:"+last14Bytes);
					byte buf[] = last14Bytes.getBytes();
					for(byte b:buf){
						if(b >= '0' && b <= '9'){
							
						}else{
							isISBNError = true;
							break;
						}
					}
				}
			}
			
			if(isISBNError){
				errorList.add(bean);
				continue;
			}
		}// for -- end.
		System.out.println("\n\n\n  error data size:"+errorList.size());
		//对于出错的数据，只能在全部检查完以后才一起删，若边检查边删，又不对索引作减1处理，则会影响到后续的检查工作。
		list.removeAll(errorList);
//		sort(errorList); //错误的数据不需要进行排序处理。
		list.addAll(list.size(), errorList);
	}

	/**
	 * 将排好序的数据输出到books_sort.txt文件中。
	 * */
	public void out(ArrayList<BookInfoBean> list, String out) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(out);
			for(BookInfoBean bean:list){
				//检查格式错误的数据咯。。
				if(bean.getType() == 1 && bean.getSerial() == 1 && bean.getName() == null){
					fos.write(bean.getIsbn().getBytes());
					fos.write("\n".getBytes());
				}else{
					StringBuilder s = new StringBuilder();
					//封装数据。
					s.append(bean.getType());
					s.append("\t");
					s.append(bean.getSerial());
					s.append("\t");
					s.append(bean.getName());
					s.append("\t");
					s.append(bean.getIsbn());
					s.append("\n");
					
					byte buf[] = s.toString().getBytes();
					
					fos.write(buf);
				}//else  --  end.
			}//for  --  end.
			fos.flush();
			
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("write file end."+out);
	}
}
