package Bookshelf.controller;

import Bookshelf.domain.Books;
import Bookshelf.domain.DBFile;
import Bookshelf.domain.DBFilePDF;
import Bookshelf.domain.User;
import Bookshelf.repos.BookRepo;
import Bookshelf.repos.DBFilePDFRepository;
import Bookshelf.repos.DBFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Service
public class FolderController {

    @Autowired
    private BookRepo bookRepo;
    @Autowired
    private DBFileRepository fileRepo;
    @Autowired
    private DBFilePDFRepository fileRepoPDF;

    @GetMapping("/addfromfile")
    public String addFromFile(@AuthenticationPrincipal User user) throws IOException {
        File folderListFolder = new File("txt/");
        String[] folderList = folderListFolder.list();
        assert folderList != null;
        for (String folderDir : folderList) {
            File folderForReading = new File("txt/" + folderDir);
            if (!folderForReading.getName().contains("txt") && !folderForReading.getName().contains("pdf") && !folderForReading.getName().contains("jpg")) {

                File folder = new File("txt/" + folderDir + "/");
                String[] files = folder.list();
                List<Books> books = null;
                assert files != null;

                for (String file : files) {
                    File fileForReading = new File("txt/" + folderDir + "/" + file);

                    if (fileForReading.getName().contains("txt")) {

                        BufferedReader bufferedReader = new BufferedReader(new FileReader(fileForReading));

                        StringBuilder stringBuffer = new StringBuilder();
                        String line = null;

                        while ((line = bufferedReader.readLine()) != null) {

                            stringBuffer.append(line).append("\n");
                        }

                        String[] words;
                        words = stringBuffer.toString().split("[\\n \\s]");

                        Map<String, String> stringToMap = new HashMap<>();
                        for (int i = 0; i < words.length; i = i + 2) {
                            stringToMap.put(words[i], words[i + 1]);
                        }
                        books = bookRepo.findByBookName(stringToMap.get("bookName"));

                        if (books.size() == 0) {
                            String bookName = null;
                            String bookAuthor = null;
                            String bookDescription = null;
                            Books book = new Books(bookName, bookAuthor, user, bookDescription);
                            book.setBookName(stringToMap.get("bookName"));
                            book.setBookAuthor(stringToMap.get("bookAuthor"));
                            book.setBookDescription(stringToMap.get("bookDescription"));
                            book.setAuthor(user);
                            bookRepo.save(book);
                            books = bookRepo.findByBookName(stringToMap.get("bookName"));
                        }
                    }
                }

                for (String file : files) {
                    File fileForReading = new File("txt/" + folderDir + "/" + file);
                    assert books != null;

                    if (fileForReading.getName().contains("jpg") && fileRepo.findByBookId(books.get(0).getId()).size() == 0) {

                        Integer bookId = books.get(0).getId();
                        String fileName = fileForReading.getName();
                        String fileType = "image/jpeg";

                        BufferedImage originalImage = ImageIO.read(new File(String.valueOf(fileForReading)));
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(originalImage, "jpg", baos);
                        baos.flush();
                        byte[] data = baos.toByteArray();
                        baos.close();

                        DBFile dbFile = new DBFile(bookId, fileName, fileType, data);
                        dbFile.setBookId(bookId);
                        dbFile.setData(data);
                        dbFile.setFileName(fileName);
                        dbFile.setFileType(fileType);
                        fileRepo.save(dbFile);
                    }
                    if (fileForReading.getName().contains("pdf") && fileRepoPDF.findByBookId(books.get(0).getId()).size() == 0) {

                        Integer bookId = books.get(0).getId();
                        String fileName = fileForReading.getName();
                        String fileType = "application/pdf";

                        FileInputStream input = new FileInputStream(fileForReading);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();

                        byte[] data;

                        byte[] buff = new byte[2048000];
                        for (int readNum; (readNum = input.read(buff)) != -1; ) {
                            baos.write(buff, 0, readNum);
                        }

                        data = baos.toByteArray();

                        DBFilePDF dbFilePdf = new DBFilePDF(bookId, fileName, fileType, data);
                        dbFilePdf.setBookId(bookId);
                        dbFilePdf.setData(data);
                        dbFilePdf.setFileName(fileName);
                        dbFilePdf.setFileType(fileType);
                        fileRepoPDF.save(dbFilePdf);
                    }
                }
            }
        }
        return "redirect:/main";
    }
}