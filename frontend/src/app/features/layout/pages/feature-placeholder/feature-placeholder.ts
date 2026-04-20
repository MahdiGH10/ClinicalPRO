import { Component, computed, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-feature-placeholder',
  standalone: true,
  templateUrl: './feature-placeholder.html',
  styleUrl: './feature-placeholder.scss'
})
export class FeaturePlaceholderComponent {
  private readonly route = inject(ActivatedRoute);

  protected readonly title = computed(() => this.route.snapshot.data['title'] ?? 'Feature');
  protected readonly description = computed(
    () => this.route.snapshot.data['description'] ?? 'This section is ready for your next slice.'
  );
}
