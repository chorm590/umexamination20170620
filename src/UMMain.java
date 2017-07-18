import java.io.File;
import java.util.ArrayList;

public class UMMain {
	
	public static final String RECORD10 = "./10records/";
	public static final String RECORD50 = "./50records/";
	public static final String RECORD150 = "./150records/";
	public static final String RECORD_ERROR = "./error_records/";
	
	public static final String INPUT = "books.txt";
	public static final String OUTPUT = "books_sort.txt";
	
	private ArrayList<BookInfoBean> list10;
	private ArrayList<BookInfoBean> list50;
	private ArrayList<BookInfoBean> list150;
	private ArrayList<BookInfoBean> listError;
	
	private DataManager dataMana;
	private static long start;
	private static long end;

	public static void main(String[] args) {
		start = System.currentTimeMillis();
		
		UMMain main = new UMMain();
		main.init();
		
		if(args.length > 0) {
			main.checkProgram();
		}else {
			main.begin();
		}
		
		end = System.currentTimeMillis();
		System.out.println("\n\n\t\tProgram end!!! [ "+(end - start)+" ms]");
	}// main()  --  end.

	/**
	 * 初始化。
	 * */
	private void init() {
		list10 = new ArrayList<>();
		list50 = new ArrayList<>();
		list150 = new ArrayList<>();
		listError = new ArrayList<>();
		
		dataMana = new DataManager();
	}

	private void begin() {
		//解析10条记录的
		dataMana.prepareRecords(list10, RECORD10+INPUT);
		dataMana.sort(list10);
		dataMana.check(list10);
		dataMana.out(list10, RECORD10+OUTPUT);
		
		//解析50条记录的
		dataMana.prepareRecords(list50, RECORD50+INPUT);
		dataMana.sort(list50);
		dataMana.check(list50);
		dataMana.out(list50, RECORD50+OUTPUT);
		
		//解析150条记录的
		dataMana.prepareRecords(list150, RECORD150+INPUT);
		dataMana.sort(list150);
		dataMana.check(list150);
		dataMana.out(list150, RECORD150+OUTPUT);
		
		//解析有错误数据记录的
		dataMana.prepareRecords(listError, RECORD_ERROR+INPUT);
		dataMana.sort(listError);
		dataMana.check(listError);
		dataMana.out(listError, RECORD_ERROR+OUTPUT);
	}
	
	private void checkProgram() {
		System.out.println("Checking your program...");
		ArrayList<BookInfoBean> listChk = new ArrayList<>();
		// Check with 'check' folder's files.
		File checkFolder = new File("./check/");
		File[] checkSubFolder = checkFolder.listFiles();
		int counter = 1;
		for(File sub:checkSubFolder) {
			System.out.println("The "+ counter++ +" to check:"+sub.getPath());
			if(sub.isDirectory()) {
				listChk.clear();
				dataMana.prepareRecords(listChk, sub.getPath()+"/"+INPUT);
				dataMana.sort(listChk);
				dataMana.check(listChk);
				dataMana.out(listChk, sub.getPath()+"/"+OUTPUT);
			}
		}
	}

}
