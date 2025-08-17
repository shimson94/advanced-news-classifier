import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


class GloveTest {
    String word = "HelloWorld";
    Vector vector = new Vector(new double[]{1,2,3});
    Glove glove = new Glove(word, vector);

    @Test
    void getVocabulary() {
        assertEquals("HelloWorld",glove.getVocabulary());
    }

    @Test
    void setVocabulary() {
        glove.setVocabulary("ModifiedText");
        assertEquals("ModifiedText",glove.getVocabulary());
    }

   @Test
    void getVector() {
        assertEquals("1.00000,2.00000,3.00000",glove.getVector().toString());
    }

    @Test
    void setVector() {
        glove.setVector(new Vector(new double[]{4,5,6}));
        assertEquals("4.00000,5.00000,6.00000",glove.getVector().toString());
    }
}