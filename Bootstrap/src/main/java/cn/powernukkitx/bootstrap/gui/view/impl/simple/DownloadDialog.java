package cn.powernukkitx.bootstrap.gui.view.impl.simple;

import cn.powernukkitx.bootstrap.gui.model.impl.CommonModel;
import cn.powernukkitx.bootstrap.util.LanguageUtils;
import cn.powernukkitx.bootstrap.util.StringUtils;
import com.github.kevinsawicki.http.HttpRequest;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import static javax.swing.SwingConstants.HORIZONTAL;

public final class DownloadDialog extends JDialog {
    private final String name;
    private final String link;
    private final File target;
    private final Consumer<DownloadDialog> callback;
    private final DownloadDialog self = this;

    private JLabel label;

    DownloadDialog(String name, String link, File target, Consumer<DownloadDialog> callback) {
        this.name = name;
        this.link = link;
        this.target = target;
        this.callback = callback;
        init();
    }

    private void init() {
        {
            this.setTitle(LanguageUtils.tr("gui.download-dialog.title", name));
            this.setSize(600, 108);
            this.setLayout(null);
            this.setVisible(true);
            this.setResizable(false);
        }
        final JPanel panel = new JPanel(null);
        this.setContentPane(panel);
        label = new JLabel(link);
        {
            panel.add(label);
            label.setSize(400, 36);
            label.setLocation(8, 0);
        }
        final JProgressBar progressBar = new JProgressBar(0, 100);
        {
            progressBar.setStringPainted(true);
            progressBar.setOrientation(HORIZONTAL);
            panel.add(progressBar);
            progressBar.setLocation(8, 36);
            progressBar.setSize(new Dimension(556, 16));
        }
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                final File dir = target.getParentFile();
                if (dir != null && !dir.exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    dir.mkdirs();
                }
                final HttpRequest request = HttpRequest.get(link);
                final AtomicLong atomicLong = new AtomicLong(0);
                CommonModel.TIMER.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        final long finished = target.length();
                        final long total = request.getConnection().getContentLength();
                        final long speed = finished - atomicLong.get();
                        atomicLong.set(finished);
                        SwingUtilities.invokeLater(() -> {
                            progressBar.setValue((int) Math.min(100, (finished * 1.0 / total) * 100));
                            label.setText(StringUtils.displayableBytes(speed) + "/s - " + link);
                        });
                        if (finished == total) {
                            this.cancel();
                        }
                    }
                }, 500, 500);
                request.receive(target);
                return null;
            }

            @Override
            protected void done() {
                self.dispose();
                callback.accept(self);
            }
        }.execute();
    }

    public static void openDownloadDialog(String name, String link, File target, Consumer<DownloadDialog> callback) {
        final DownloadDialog dialog = new DownloadDialog(name, link, target, callback);
        dialog.setLocationRelativeTo(null);
    }

    @Override
    public String getName() {
        return name;
    }

    public String getLink() {
        return link;
    }

    public File getTarget() {
        return target;
    }

    public Consumer<DownloadDialog> getCallback() {
        return callback;
    }

    public JLabel getLabel() {
        return label;
    }
}
