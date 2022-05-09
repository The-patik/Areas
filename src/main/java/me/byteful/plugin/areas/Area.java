package me.byteful.plugin.areas;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class Area {
  private String name, title, subtitle, actionbar;
  private String world;
  private List<RegionPoint2D> points;
  private int minY, maxY;

  public Area() {}

  public Area(@NotNull String name, @NotNull String title, @NotNull String subtitle, @NotNull String actionbar, @NotNull World world, @NotNull List<RegionPoint2D> points, int minY, int maxY) {
    this.name = name;
    this.title = title;
    this.subtitle = subtitle;
    this.actionbar = actionbar;
    this.world = world.getName();
    this.points = points;
    this.minY = minY;
    this.maxY = maxY;
  }

  public List<RegionPoint2D> getPoints() {
    return points;
  }

  public int getMinY() {
    return minY;
  }

  public int getMaxY() {
    return maxY;
  }

  public String getName() {
    return name;
  }

  public String getTitle() {
    return title;
  }

  public String getSubtitle() {
    return subtitle;
  }

  public String getActionbar() {
    return actionbar;
  }

  public String getWorld() {
    return world;
  }

  public boolean contains(@NotNull Location location) {
    return location.getWorld() != null && location.getWorld().getName().equals(world) && contains(new RegionPoint3D(location));
  }

  public boolean contains(@NotNull RegionPoint3D pt) {
    if (points.size() < 3) {
      return false;
    }
    int targetX = pt.getBlockX(); //wide
    int targetY = pt.getBlockY(); //height
    int targetZ = pt.getBlockZ(); //depth

    if (targetY < minY || targetY > maxY) {
      return false;
    }

    boolean inside = false;
    int npoints = points.size();
    int xNew;
    int zNew;
    int x1;
    int z1;
    int x2;
    int z2;
    long crossproduct;
    int i;

    int xOld = points.get(npoints - 1).getBlockX();
    int zOld = points.get(npoints - 1).getBlockZ();

    for (i = 0; i < npoints; ++i) {
      xNew = points.get(i).getBlockX();
      zNew = points.get(i).getBlockZ();
      //Check for corner
      if (xNew == targetX && zNew == targetZ) {
        return true;
      }
      if (xNew > xOld) {
        x1 = xOld;
        x2 = xNew;
        z1 = zOld;
        z2 = zNew;
      } else {
        x1 = xNew;
        x2 = xOld;
        z1 = zNew;
        z2 = zOld;
      }
      if (x1 <= targetX && targetX <= x2) {
        crossproduct = ((long) targetZ - (long) z1) * (long) (x2 - x1)
          - ((long) z2 - (long) z1) * (long) (targetX - x1);
        if (crossproduct == 0) {
          if ((z1 <= targetZ) == (targetZ <= z2)) {
            return true; //on edge
          }
        } else if (crossproduct < 0 && (x1 != targetX)) {
          inside = !inside;
        }
      }
      xOld = xNew;
      zOld = zNew;
    }

    return inside;
  }

  public void save(AreasPlugin plugin) {
    plugin.getAreas().put(name, this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Area area = (Area) o;
    return minY == area.minY && maxY == area.maxY && name.equals(area.name) && title.equals(area.title) && subtitle.equals(area.subtitle) && actionbar.equals(area.actionbar) && world.equals(area.world) && points.equals(area.points);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, title, subtitle, actionbar, world, points, minY, maxY);
  }

  @Override
  public String toString() {
    return "Area{" +
      ", name='" + name + '\'' +
      ", title='" + title + '\'' +
      ", subtitle='" + subtitle + '\'' +
      ", actionbar='" + actionbar + '\'' +
      ", world='" + world + '\'' +
      ", points=" + points +
      ", minY=" + minY +
      ", maxY=" + maxY +
      '}';
  }

  public static class RegionPoint2D {
    private int x, z;

    public RegionPoint2D() {}

    public RegionPoint2D(int x, int z) {
      this.x = x;
      this.z = z;
    }

    public RegionPoint2D(@NotNull Location location) {
      this(location.getBlockX(), location.getBlockZ());
    }

    public int getBlockX() {
      return x;
    }

    public int getBlockZ() {
      return z;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      RegionPoint2D that = (RegionPoint2D) o;
      return x == that.x && z == that.z;
    }

    @Override
    public int hashCode() {
      return Objects.hash(x, z);
    }

    @Override
    public String toString() {
      return "RegionPoint2D{" +
        "x=" + x +
        ", z=" + z +
        '}';
    }
  }

  public static class RegionPoint3D {
    private int x, y, z;

    public RegionPoint3D() {}

    public RegionPoint3D(int x, int y, int z) {
      this.x = x;
      this.y = y;
      this.z = z;
    }

    public RegionPoint3D(@NotNull Location location) {
      this(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public int getBlockX() {
      return x;
    }

    public int getBlockY() {
      return y;
    }

    public int getBlockZ() {
      return z;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      RegionPoint3D that = (RegionPoint3D) o;
      return x == that.x && y == that.y && z == that.z;
    }

    @Override
    public int hashCode() {
      return Objects.hash(x, y, z);
    }

    @Override
    public String toString() {
      return "RegionPoint3D{" +
        "x=" + x +
        ", y=" + y +
        ", z=" + z +
        '}';
    }
  }
}
