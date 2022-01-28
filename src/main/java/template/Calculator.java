package template;

import template.callback.BufferedReaderCallback;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Calculator {
    public Integer calcSum(String filepath) throws IOException {
        return fileReadTemplate(filepath, new BufferedReaderCallback() {
            @Override
            public Integer workWithReader(BufferedReader br) throws IOException {
                String line = null;
                Integer sum = 0;
                while((line = br.readLine()) != null) {
                    sum+=Integer.parseInt(line);
                }

                return sum;
            }
        });
    }

    public Integer calcMultiply(String filepath) throws IOException {
        return fileReadTemplate(filepath, new BufferedReaderCallback() {
            @Override
            public Integer workWithReader(BufferedReader br) throws IOException {
                String line = null;
                Integer product = 1;

                while((line = br.readLine()) != null) {
                    product*=Integer.parseInt(line);
                }

                return product;
            }
        });
    }

    public Integer fileReadTemplate(String filepath, BufferedReaderCallback callback)
    throws IOException {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filepath));
            int ret = callback.workWithReader(br);
            return ret;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw e;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }
}
