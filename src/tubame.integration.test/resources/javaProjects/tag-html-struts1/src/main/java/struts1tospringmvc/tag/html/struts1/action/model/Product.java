package struts1tospringmvc.tag.html.struts1.action.model;

/**
 * フォームのフィールドが String やプリミティブ型ばかりではつまらない時や、わかりづらい時に使うモデル
 * @author tanabe
 */
public class Product {

  private int id;
  private String name;
  private boolean like = true;
  private int amount;

  public Product(int id, String name) {
    this(id, name, 0);
  }

  public Product(int id, String name, int amount) {
    this.id = id;
    this.name = name;
    this.amount = amount;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isLike() {
    return like;
  }

  public void setLike(boolean like) {
    this.like = like;
  }

  public int getAmount() {
    return amount;
  }

  public void setAmount(int amount) {
    this.amount = amount;
  }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Product product = (Product) o;

    if (id != product.id) return false;
    if (like != product.like) return false;
    if (name != null ? !name.equals(product.name) : product.name != null) return false;

    return true;
  }

  public int hashCode() {
    int result = id;
    result = 31 * result + (name != null ? name.hashCode() : 0);
    result = 31 * result + (like ? 1 : 0);
    return result;
  }

  public String toString() {
    return "Product{" +
      "id=" + id +
      ", name='" + name + '\'' +
      ", like=" + like +
      '}';
  }
}
