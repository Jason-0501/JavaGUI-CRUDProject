import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.google.gson.JsonObject;
import com.ibm.icu.impl.coll.Collation;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.Filters;
import com.oracle.truffle.js.builtins.JSONBuiltins.JSON;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

public class project{
    static MongoClient mongoclient = new MongoClient("localhost",27017);
   
    public static void main(String[] args)throws Exception{

        // 資料庫連線
        MongoDatabase mongoDatabase = mongoclient.getDatabase("finalProject");
        // mongoDatabase.createCollection("grade",new CreateCollectionOptions().capped(true).sizeInBytes(0x100000));
        MongoCollection<Document> collection = mongoDatabase.getCollection("grade");

        JFrame frame = new JFrame("成績輸入");
        frame.setBounds(550,20,500,800);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(null);

        JLabel title = new JLabel("成績輸入系統");
        title.setBounds(190,20,150,20);
        title.setFont(new java.awt.Font("Dialog", 1, 18));

        JLabel id = new JLabel("姓名：");
        id.setFont(new java.awt.Font("Dialog", 1, 15));
        id.setBounds(120,50,50,20);

        JLabel chinese = new JLabel("國文：");
        chinese.setFont(new java.awt.Font("Dialog", 1, 15));
        chinese.setBounds(120,80,50,20);
        
        JLabel english = new JLabel("英文：");
        english.setFont(new java.awt.Font("Dialog", 1, 15));
        english.setBounds(120,110,50,20);

        JLabel math = new JLabel("數學：");
        math.setFont(new java.awt.Font("Dialog", 1, 15));
        math.setBounds(120,140,50,20);

        JLabel search = new JLabel("成績查詢專欄");
        search.setBounds(190,330,150,20);
        search.setFont(new java.awt.Font("Dialog", 1, 18));
        
        JLabel detail = new JLabel("姓名        國文        英文        數學");
        detail.setBounds(120,360,300,20);
        detail.setFont(new java.awt.Font("Dialog", 1, 15));

        JTextArea showdetail = new JTextArea();
        showdetail.setBounds(120,400,225,300);

        JTextArea type_id = new JTextArea();
        type_id.setBounds(172,53,140,20);

        JTextArea type_chinese = new JTextArea();
        type_chinese.setBounds(172,83,140,20);

        JTextArea type_english = new JTextArea();
        type_english.setBounds(172,113,140,20);

        JTextArea type_math = new JTextArea();
        type_math.setBounds(172,143,140,20);

        JButton submit = new JButton(new AbstractAction("提交成績") {
            @Override
            public void actionPerformed(ActionEvent e) {
                String idvalue = type_id.getText();
                String chinesevalue = type_chinese.getText();
                String englishvalue = type_english.getText();
                String mathvalue = type_math.getText();

                // 將成績輸入到資料庫
                Document document = new Document("姓名",idvalue)
                .append("Chinese",chinesevalue)
                .append("English",englishvalue)
                .append("Math", mathvalue);
                collection.insertOne(document);

                type_id.setText("");
                type_chinese.setText("");
                type_english.setText("");
                type_math.setText("");
                JOptionPane.showMessageDialog(frame, String.format("成功提交%s同學的成績", idvalue), "提交成績", 1);
                
            }
            
        });
        submit.setBounds(173,173,140,20);
        submit.setBackground(Color.WHITE);
        submit.setFont(new java.awt.Font("Dialog", 1, 12));

        JButton read = new JButton(new AbstractAction("查詢成績") {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                String name = JOptionPane.showInputDialog("欲查詢哪位同學的成績?");
                
                FindIterable<Document> findIterable = collection.find(Filters.eq("姓名", name)); 

                Document detail = findIterable.first();
                String chinese = detail.getString("Chinese");
                String english = detail.getString("English");
                String math = detail.getString("Math");
                String information="";
                information = information.concat(name+"           ")
                .concat(chinese+"               ")
                .concat(english+"               ")
                .concat(math);
                showdetail.setText(information);
            }
        });
        read.setBounds(173,203,140,20);
        read.setBackground(Color.WHITE);
        read.setFont(new java.awt.Font("Dialog", 1, 12));

        JButton readAll = new JButton(new AbstractAction("查詢全部成績") {
            @Override
            public void actionPerformed(ActionEvent e) {

                FindIterable<Document> findIterable = collection.find();  
                MongoCursor<Document> mongoCursor = findIterable.iterator();
                String information = "";
                while(mongoCursor.hasNext()){
                    Document detail = mongoCursor.next();
                    String name = detail.getString("姓名");
                    String chinese = detail.getString("Chinese");
                    String english = detail.getString("English");
                    String math = detail.getString("Math");

                    information=information.concat(name+"           ")
                    .concat(chinese+"               ")
                    .concat(english+"               ")
                    .concat(math+"\n");
                }
                showdetail.setText(information);
            }
            
        });
        readAll.setBounds(173,233,140,20);
        readAll.setBackground(Color.WHITE);
        readAll.setFont(new java.awt.Font("Dialog", 1, 12));

        JButton update = new JButton(new AbstractAction("更改成績") {
            @Override
            public void actionPerformed(ActionEvent e) {
                String idvalue = type_id.getText();
                String chinesevalue = type_chinese.getText();
                String englishvalue = type_english.getText();
                String mathvalue = type_math.getText();

                // 將想更改的資料傳入資料庫進行更改
                if(!chinesevalue.equals("")){
                    collection.updateOne(Filters.eq("姓名", idvalue), new Document("$set",new Document("Chinese",chinesevalue)));
                }

                if(!englishvalue.equals("")){
                    collection.updateOne(Filters.eq("姓名", idvalue), new Document("$set",new Document("English",englishvalue)));
                }

                if(!mathvalue.equals("")){
                    collection.updateOne(Filters.eq("姓名", idvalue), new Document("$set",new Document("Math",mathvalue)));
                }

                type_id.setText("");
                type_chinese.setText("");
                type_english.setText("");
                type_math.setText("");
                JOptionPane.showMessageDialog(frame, String.format("成功更改%s同學的成績", idvalue), "更改成績", 1);
            }
        });
        update.setBounds(173,263,140,20);
        update.setBackground(Color.WHITE);
        update.setFont(new java.awt.Font("Dialog", 1, 12));

        JButton delete = new JButton(new AbstractAction("刪除成績") {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = JOptionPane.showInputDialog("欲刪除哪位同學的成績?");
                collection.deleteOne(Filters.eq("姓名",name));
                JOptionPane.showMessageDialog(frame, String.format("成功刪除%s同學的成績", name), "刪除成績", 1);
            }
            
        });

        delete.setBounds(173,293,140,20);
        delete.setBackground(Color.WHITE);
        delete.setFont(new java.awt.Font("Dialog", 1, 12));


        frame.add(title);
        frame.add(id);
        frame.add(detail);
        frame.add(showdetail);
        frame.add(chinese);
        frame.add(english);
        frame.add(math);
        frame.add(search);

        frame.add(type_id);
        frame.add(type_chinese);
        frame.add(type_english);
        frame.add(type_math);

        frame.add(submit);
        frame.add(read);
        frame.add(readAll);
        frame.add(update);
        frame.add(delete);


        frame.setVisible(true);
    }
}