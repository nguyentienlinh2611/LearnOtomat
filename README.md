# LearnOtomat
Chương trình được viết bằng Java. Gồm có các thuật toán tối tiểu hóa Otomat đơn định hữu hạn, kiểm tra đoán nhận xâu của otomat đơn định hữu hạn, thuật toán CKY kiểm tra đoán nhận xâu của tập quy tắc sinh của ngôn ngữ phi ngữ cảnh.
## Deterministic finite automata
Tạo một otomat đơn định hữu hạn  
`Otomat myOtomat = new Otomat();`  
Thêm hàm chuyển cho otomat  
`Otomat.addTransition(src, dest, word);`  
Với src là trạng thái xuất phát, dest là trạng thái đích và word là từ được sinh ra  
Đặt trạng thái bắt đầu  
`Otomat.setStart(Start State);  `  
Đặt tập trạng thái kết thúc  
`Otomat.setStart(Finish States);`  
### Lưu ý:
Các trạng thái phải là kí tự (Character) in hoa. Và Otomat là đơn định.  
Ví dụ tạo otomat như sau:  

```
Otomat myOtomat = new Otomat();  
myOtomat.addTransition('A', 'B', '0');  
myOtomat.addTransition('A', 'F', '1');  
myOtomat.addTransition('B', 'C', '1');  
myOtomat.addTransition('B', 'G', '0');  
myOtomat.addTransition('C', 'C', '1');  
myOtomat.addTransition('C', 'A', '0');  
myOtomat.addTransition('D', 'C', '0');  
myOtomat.addTransition('D', 'G', '1');  
myOtomat.addTransition('E', 'F', '1');  
myOtomat.addTransition('E', 'H', '0');  
myOtomat.addTransition('F', 'C', '0');  
myOtomat.addTransition('F', 'G', '1');  
myOtomat.addTransition('G', 'E', '1');  
myOtomat.addTransition('G', 'G', '0');  
myOtomat.addTransition('H', 'G', '0');  
myOtomat.addTransition('H', 'C', '1');  
myOtomat.setStart('A');  
ArrayList<Character> finish = new ArrayList<Character>();  
finish.add('C');  
myOtomat.setFinish(finish);  
```  
Kiểm tra xem xâu đầu vào có được đoán nhận bởi otomat đơn định hữu hạn hay không  
`Otomat.CheckAccept(string)`  
## CKY  
Tạo đối tượng CKY với xâu đầu vào cần kiểm tra.  
`CKY cky = new CKY(input);`  
Thêm quy tắc sinh  S -> A B.  
`cky.addProductionRule("S -> A B");`  
### Lưu ý:  
Quy tắc sinh phải ở dạng chuẩn Chomsky.  
Các trạng thái trong quy tắc sinh phải được phân biệt với nhau bởi dấu cách.  
Ký hiệu chuyển phải là ->  
Trạng thái bắt đầu phải là S  

